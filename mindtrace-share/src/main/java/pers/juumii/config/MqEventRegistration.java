package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.EnhancerShareMapper;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.mapper.ResourceShareMapper;
import pers.juumii.mq.MessageEvents;

import java.util.List;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final KnodeShareMapper knodeShareMapper;
    private final EnhancerShareMapper enhancerShareMapper;
    private final ResourceShareMapper resourceShareMapper;
    private final EnhancerClient enhancerClient;
    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;

    @Autowired
    public MqEventRegistration(
            KnodeShareMapper knodeShareMapper,
            EnhancerShareMapper enhancerShareMapper,
            ResourceShareMapper resourceShareMapper,
            EnhancerClient enhancerClient,
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient) {
        this.knodeShareMapper = knodeShareMapper;
        this.enhancerShareMapper = enhancerShareMapper;
        this.resourceShareMapper = resourceShareMapper;
        this.enhancerClient = enhancerClient;
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
    }

    public void handleRemoveKnode(String knodeString){
        KnodeDTO knode = JSONUtil.toBean(knodeString, KnodeDTO.class);
        List<EnhancerDTO> enhancers =
            enhancerClient.getEnhancersOfKnode(Convert.toLong(knode.getId()));
        for(EnhancerDTO enhancer: enhancers)
            handleRemoveEnhancer(enhancer.getId());
        knodeShareMapper.deleteByKnodeId(Convert.toLong(knode.getId()));
    }

    public void handleRemoveEnhancer(String idString){
        Long enhancerId = Convert.toLong(idString);
        if(enhancerId == null) return;
        List<ResourceDTO> resources =
            enhancerClient.getResourcesOfEnhancer(enhancerId);
        for(ResourceDTO resource: resources)
            handleRemoveResource(resource.getId());
        enhancerShareMapper.deleteByEnhancerId(Convert.toLong(enhancerId));
    }

    public void handleRemoveResource(String resourceId){
        resourceShareMapper.deleteByResourceId(Convert.toLong(resourceId));
    }


    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-share");
        String removeKnodeUrl = self.getUri().toString() + "/mq/knode/remove";
        String removeEnhancerUrl = self.getUri().toString() + "/mq/enhancer/remove";
        String removeResourceUrl = self.getUri().toString() + "/mq/resource/remove";
        mqClient.addListener(MessageEvents.REMOVE_KNODE, "mindtrace-share::handleRemoveKnode", removeKnodeUrl);
        mqClient.addListener(MessageEvents.REMOVE_ENHANCER, "mindtrace-share::handleRemoveEnhancer", removeEnhancerUrl);
        mqClient.addListener(MessageEvents.REMOVE_RESOURCE, "mindtrace-share::handleRemoveResource", removeResourceUrl);
    }
}
