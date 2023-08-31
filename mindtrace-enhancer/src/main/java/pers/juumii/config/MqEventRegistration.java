package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.EnhancerService;

import java.util.List;

@Component
public class MqEventRegistration implements ApplicationRunner {

    public static final String REMOVE_KNODE_LISTENER = "handleRemoveKnode";
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final EnhancerService enhancerService;
    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;

    @Autowired
    public MqEventRegistration(
            EnhancerKnodeRelationshipMapper ekrMapper,
            EnhancerService enhancerService,
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient) {
        this.ekrMapper = ekrMapper;
        this.enhancerService = enhancerService;
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
    }

    public void handleRemoveKnode(String message){
        KnodeDTO knode = JSONUtil.toBean(message, KnodeDTO.class);
        Long knodeId = Convert.toLong(knode.getId());
        List<EnhancerKnodeRel> rels = ekrMapper.getByKnodeId(knodeId);
        for(EnhancerKnodeRel rel : rels)
            enhancerService.disconnectEnhancerFromKnode(knodeId,rel.getEnhancerId());
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-enhancer");
        String targetUrl = self.getUri().toString() + "/mq/knode/remove";
        mqClient.addListener(MessageEvents.REMOVE_KNODE, REMOVE_KNODE_LISTENER, targetUrl);
    }
}
