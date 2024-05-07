package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.EnhancerGroupService;
import pers.juumii.service.EnhancerService;

import java.util.List;
import java.util.Objects;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final EnhancerService enhancerService;
    private final EnhancerGroupService enhancerGroupService;
    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;
    private final StringRedisTemplate redis;


    @Autowired
    public MqEventRegistration(
            EnhancerKnodeRelationshipMapper ekrMapper,
            EnhancerService enhancerService,
            EnhancerGroupService enhancerGroupService,
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient,
            StringRedisTemplate redis) {
        this.ekrMapper = ekrMapper;
        this.enhancerService = enhancerService;
        this.enhancerGroupService = enhancerGroupService;
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
        this.redis = redis;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-enhancer");
        mqClient.addListener(
                MessageEvents.REMOVE_KNODE,
                MessageEvents.buildUrl(self, "/mq/knode/remove"));
        mqClient.addListener(
                MessageEvents.REMOVE_ENHANCER,
                MessageEvents.buildUrl(self, "/mq/enhancer/remove"));
        mqClient.addListener(
                MessageEvents.REMOVE_RESOURCE,
                MessageEvents.buildUrl(self, "/mq/resource/remove"));
        mqClient.addListener(
                MessageEvents.ADD_DATA_TO_RESOURCE,
                MessageEvents.buildUrl(self, "/mq/data/add"));
    }

    public void handleRemoveKnode(String message){
        KnodeDTO knode = JSONUtil.toBean(message, KnodeDTO.class);
        Long knodeId = Convert.toLong(knode.getId());
        List<EnhancerKnodeRel> rels = ekrMapper.getByKnodeId(knodeId);
        for(EnhancerKnodeRel rel : rels)
            enhancerService.removeKnodeEnhancerRel(knodeId,rel.getEnhancerId());
    }

    public void handleRemoveEnhancer(String message){
        Long enhancerId = Convert.toLong(message);
        List<Long> groupIds = enhancerGroupService.getEnhancerGroupIdsByEnhancerId(enhancerId);
        groupIds.forEach(groupId->enhancerGroupService.removeEnhancerGroupRel(enhancerId, groupId));
    }

    public void handleRemoveResource(String message){
        Long resourceId = Convert.toLong(message);
        List<Long> groupIds = enhancerGroupService.getEnhancerGroupIdsByResourceId(resourceId);
        groupIds.forEach(groupId->enhancerGroupService.removeEnhancerGroupResourceRel(groupId, resourceId));
    }


    public void handleAddDataToResource(String data) {
        String key = RedisKeys.RESOURCE_VECTOR_UPDATE_LIST;
        Long resourceId = Convert.toLong(data);
        String raw = redis.opsForValue().get(key);
        if(Objects.isNull(raw)) raw = "[]";
        List<Long> resourceIds = JSONUtil.toList(raw, Long.class);
        if(resourceIds.contains(resourceId)) return;
        resourceIds.add(resourceId);
        redis.opsForValue().set(key, JSONUtil.toJsonStr(resourceIds));
    }
}
