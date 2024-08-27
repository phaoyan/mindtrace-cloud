package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.mq.MessageEvents;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;
    private final TraceEnhancerRelMapper terMapper;

    @Autowired
    public MqEventRegistration(
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient,
            TraceEnhancerRelMapper terMapper) {
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
        this.terMapper = terMapper;
    }

    public void handleRemoveKnode(String data) {
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
