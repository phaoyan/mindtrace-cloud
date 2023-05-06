package pers.juumii.mq;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.data.EnhancerShare;
import pers.juumii.data.KnodeShare;
import pers.juumii.data.ResourceShare;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.EnhancerShareMapper;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.mapper.ResourceShareMapper;
import pers.juumii.mapper.UserShareMapper;

import java.util.List;

@Component
public class KnodeEventConsumer {

    private final UserShareMapper userShareMapper;
    private final KnodeShareMapper knodeShareMapper;
    private final EnhancerShareMapper enhancerShareMapper;
    private final ResourceShareMapper resourceShareMapper;
    private final EnhancerClient enhancerClient;

    @Autowired
    public KnodeEventConsumer(
            UserShareMapper userShareMapper,
            KnodeShareMapper knodeShareMapper,
            EnhancerShareMapper enhancerShareMapper,
            ResourceShareMapper resourceShareMapper,
            EnhancerClient enhancerClient) {
        this.userShareMapper = userShareMapper;
        this.knodeShareMapper = knodeShareMapper;
        this.enhancerShareMapper = enhancerShareMapper;
        this.resourceShareMapper = resourceShareMapper;
        this.enhancerClient = enhancerClient;
    }

    @RabbitListener(queues = KnodeExchange.ADD_KNODE_EVENT_MQ)
    public void handleAddKnode(String knodeString){
        KnodeDTO knode = JSONUtil.toBean(knodeString, KnodeDTO.class);
        Long userId = Convert.toLong(knode.getCreateBy());
        Long knodeId = Convert.toLong(knode.getId());
        if(userShareMapper.userExists(userId))
            knodeShareMapper.insert(KnodeShare.prototype(userId, knodeId));
    }

    @RabbitListener(queues = KnodeExchange.ADD_ENHANCER_EVENT_MQ)
    public void handleAddEnhancer(String message){
        EnhancerDTO enhancer = JSONUtil.toBean(message, EnhancerDTO.class);
        Long userId = Convert.toLong(enhancer.getCreateBy());
        Long enhancerId = Convert.toLong(enhancer.getId());
        if(userShareMapper.userExists(userId))
            enhancerShareMapper.insert(EnhancerShare.prototype(userId, enhancerId));
    }

    @RabbitListener(queues = KnodeExchange.ADD_RESOURCE_EVENT_MQ)
    public void handleAddResource(String message){
        ResourceDTO resource = JSONUtil.toBean(message, ResourceDTO.class);
        Long userId = Convert.toLong(resource.getCreateBy());
        Long resourceId = Convert.toLong(resource.getId());
        if(userShareMapper.userExists(userId))
            resourceShareMapper.insert(ResourceShare.prototype(userId, resourceId));
    }

    @RabbitListener(queues = KnodeExchange.REMOVE_KNODE_EVENT_MQ)
    public void handleRemoveKnode(String knodeString){
        KnodeDTO knode = JSONUtil.toBean(knodeString, KnodeDTO.class);
        List<EnhancerDTO> enhancers =
            enhancerClient.getEnhancersOfKnode(Convert.toLong(knode.getId()));
        for(EnhancerDTO enhancer: enhancers)
            handleRemoveEnhancer(enhancer.getId());
        knodeShareMapper.deleteByKnodeId(Convert.toLong(knode.getId()));
    }

    @RabbitListener(queues = KnodeExchange.REMOVE_ENHANCER_EVENT_MQ)
    public void handleRemoveEnhancer(String idString){
        Long enhancerId = Convert.toLong(idString);
        List<ResourceDTO> resources =
            enhancerClient.getResourcesOfEnhancer(enhancerId);
        for(ResourceDTO resource: resources)
            handleRemoveResource(resource.getId());
        enhancerShareMapper.deleteByEnhancerId(Convert.toLong(enhancerId));
    }

    @RabbitListener(queues = KnodeExchange.REMOVE_RESOURCE_EVENT_MQ)
    public void handleRemoveResource(String resourceId){
        resourceShareMapper.deleteByResourceId(Convert.toLong(resourceId));
    }


}
