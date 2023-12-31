package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.data.persistent.TraceKnodeRel;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.mapper.TraceKnodeRelMapper;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.StudyTraceService;

import java.util.List;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;
    private final TraceKnodeRelMapper tkrMapper;
    private final TraceEnhancerRelMapper terMapper;
    private final StudyTraceService studyTraceService;

    @Autowired
    public MqEventRegistration(
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient,
            TraceKnodeRelMapper tkrMapper,
            TraceEnhancerRelMapper terMapper,
            StudyTraceService studyTraceService) {
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
        this.tkrMapper = tkrMapper;
        this.terMapper = terMapper;
        this.studyTraceService = studyTraceService;
    }

    public void handleRemoveKnode(String data) {
        KnodeDTO knode = JSONUtil.toBean(data, KnodeDTO.class);
        Long knodeId = Convert.toLong(knode.getId());
        LambdaQueryWrapper<TraceKnodeRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TraceKnodeRel::getKnodeId, knodeId);
        List<Long> traceIds = studyTraceService.getStudyTracesOfKnode(knodeId);
        if(traceIds.isEmpty()) return;
        //将trace和被删除的knode的父节点关联，以避免记录丢失
        Long stemId = Convert.toLong(knode.getStemId());
        traceIds.forEach(traceId->studyTraceService.addTraceKnodeRel(IdPair.of(traceId, stemId)));
        //删除trace
        tkrMapper.deleteBatchIds(traceIds);
    }

    public void handleRemoveEnhancer(String data){
        Long enhancerId = Convert.toLong(data);
        LambdaUpdateWrapper<TraceEnhancerRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceEnhancerRel::getEnhancerId, enhancerId);
        terMapper.delete(wrapper);
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-tracing");
        String removeKnodeUrl = self.getUri().toString() + "/mq/knode/remove";
        String removeEnhancerUrl = self.getUri().toString() + "/mq/enhancer/remove";
        mqClient.addListener(MessageEvents.REMOVE_KNODE, removeKnodeUrl);
        mqClient.addListener(MessageEvents.REMOVE_ENHANCER, removeEnhancerUrl);
    }


}
