package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pers.juumii.data.EnhancerResourceRelationship;
import pers.juumii.data.Resource;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.GatewayClient;
import pers.juumii.feign.HubClient;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.EnhancerResourceRelationshipMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.mq.KnodeExchange;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceService;
import pers.juumii.service.impl.router.ResourceRouter;
import pers.juumii.utils.AuthUtils;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.SerialTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {


    private final AuthUtils authUtils;
    private final ResourceRouter router;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository repository;
    private final EnhancerMapper enhancerMapper;
    private final EnhancerResourceRelationshipMapper errMapper;
    private final RabbitTemplate rabbit;
    private final CoreClient coreClient;
    private final HubClient hubClient;
    private EnhancerService enhancerService;

    @Lazy
    @Autowired
    public void setEnhancerService(EnhancerService enhancerService) {
        this.enhancerService = enhancerService;
    }

    @Autowired
    public ResourceServiceImpl(
            AuthUtils authUtils,
            ResourceRouter router,
            ResourceMapper resourceMapper,
            ResourceRepository repository,
            EnhancerMapper enhancerMapper,
            EnhancerResourceRelationshipMapper errMapper,
            RabbitTemplate rabbit,
            CoreClient coreClient,
            HubClient hubClient) {
        this.authUtils = authUtils;
        this.router = router;
        this.resourceMapper = resourceMapper;
        this.repository = repository;
        this.enhancerMapper = enhancerMapper;
        this.errMapper = errMapper;
        this.rabbit = rabbit;
        this.coreClient = coreClient;
        this.hubClient = hubClient;
    }



    @Override
    public Boolean exists(Long userId, Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        return resource != null && resource.getCreateBy().equals(userId);
    }

    @Override
    public Resource getResourceMetadata(Long resourceId) {
        Resource res = resourceMapper.selectById(resourceId);
        if(res == null) return null;
        authUtils.auth(res.getCreateBy());
        return res;
    }

    @Override
    public Map<String, Object> getDataFromResource(Long resourceId) {
        Resource meta = getResourceMetadata(resourceId);
        authUtils.auth(meta.getCreateBy());
        return router.resolver(meta).resolve(meta);
    }

    @Override
    public Object getDataFromResource(Long resourceId, String dataName) {
        Resource meta = getResourceMetadata(resourceId);
        authUtils.auth(meta.getCreateBy());
        return router.resolver(meta).resolve(meta, dataName);
    }

    @Override
    public Resource addResourceToUser(Long userId, ResourceDTO metaDTO, Map<String, Object> data) {
        Resource meta = Resource.prototype(metaDTO);
        authUtils.same(meta.getCreateBy());
        resourceMapper.insert(meta);
        router.serializer(meta).serialize(meta, data);

        rabbit.convertAndSend(
                KnodeExchange.KNODE_EVENT_EXCHANGE,
                KnodeExchange.ROUTING_KEY_ADD_RESOURCE,
                JSONUtil.toJsonStr(Resource.transfer(meta)));
        return meta;
    }

    @Override
    public SaResult addDataToResource(Long resourceId, Map<String, Object> data) {
        Resource meta = getResourceMetadata(resourceId);
        authUtils.same(meta.getCreateBy());
        router.serializer(meta).serialize(meta, data);
        return SaResult.data(meta);
    }

    @Override
    public Map<String, Boolean> release(Long resourceId, List<String> data) {
        Resource meta = getResourceMetadata(resourceId);
        authUtils.same(meta.getCreateBy());
        Map<String, Boolean> deleted = new HashMap<>();
        for(String dataName: data)
            deleted.put(dataName, repository.release(meta.getCreateBy() ,resourceId, dataName));
        return deleted;
    }

    @Override
    public void removeResource(Long resourceId) {
        // 先释放该resource的所有资源文件再删除resource
        Long userId = getResourceMetadata(resourceId).getCreateBy();
        authUtils.same(userId);
        repository.releaseAll(userId, resourceId);
        resourceMapper.deleteById(resourceId);
        errMapper.deleteByResourceId(resourceId);

        rabbit.convertAndSend(
                KnodeExchange.KNODE_EVENT_EXCHANGE,
                KnodeExchange.ROUTING_KEY_REMOVE_RESOURCE,
                resourceId.toString());
    }

    @Override
    public void removeAllResourcesFromEnhancer(Long enhancerId) {
        for(Resource resource: getResourcesOfEnhancer(enhancerId))
            // removeResource中已有鉴权
            removeResource(resource.getId());
    }

    @Override
    public Resource addResourceToEnhancer(
            Long enhancerId,
            ResourceDTO meta,
            Map<String, Object> data) {
        Long userId = enhancerMapper.selectById(enhancerId).getCreateBy();
        authUtils.same(userId);
        Resource resource = addResourceToUser(userId, meta, data);
        connectResourceToEnhancer(enhancerId, resource.getId());
        return resource;
    }

    @Override
    public List<Resource> getResourcesOfEnhancer(Long enhancerId) {
        List<Long> resourceIds =
                errMapper.selectByEnhancerId(enhancerId).stream()
                .map(EnhancerResourceRelationship::getResourceId).toList();
        if(resourceIds.isEmpty()) return new ArrayList<>();
        List<Resource> resources = resourceMapper.selectBatchIds(resourceIds);
        if(resources.isEmpty()) return new ArrayList<>();
        authUtils.auth(resources.get(0).getCreateBy());
        return resources;
    }

    @Override
    public void connectResourceToEnhancer(Long enhancerId, Long resourceId) {
        EnhancerResourceRelationship target = new EnhancerResourceRelationship();
        target.setResourceId(resourceId);
        target.setEnhancerId(enhancerId);
        errMapper.insert(target);
    }

    @Override
    public void disconnectResourceFromEnhancer(Long enhancerId, Long resourceId) {
        LambdaUpdateWrapper<EnhancerResourceRelationship> wrapper = new LambdaUpdateWrapper<>();
        wrapper
                .eq(EnhancerResourceRelationship::getResourceId, resourceId)
                .eq(EnhancerResourceRelationship::getEnhancerId, enhancerId);
        errMapper.delete(wrapper);
    }

    @Override
    public List<Resource> getResourcesOfKnode(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        if(knode == null) return new ArrayList<>();
        return DataUtils.join(
            enhancerService.getEnhancersFromKnode(knodeId)
            .stream().map(enhancer -> getResourcesOfEnhancer(enhancer.getId()))
            .toList());
    }


}
