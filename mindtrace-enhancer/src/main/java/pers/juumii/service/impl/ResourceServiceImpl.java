package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.EnhancerResourceRel;
import pers.juumii.data.Resource;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.EnhancerResourceRelationshipMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.mq.KnodeExchange;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.AuthUtils;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.TimeUtils;

import java.io.InputStream;
import java.util.*;

@Service
public class ResourceServiceImpl implements ResourceService {


    private final AuthUtils authUtils;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository repository;
    private final EnhancerResourceRelationshipMapper errMapper;
    private final RabbitTemplate rabbit;
    private final CoreClient coreClient;
    private EnhancerService enhancerService;

    @Lazy
    @Autowired
    public void setEnhancerService(EnhancerService enhancerService) {
        this.enhancerService = enhancerService;
    }

    @Autowired
    public ResourceServiceImpl(
            AuthUtils authUtils,
            ResourceMapper resourceMapper,
            ResourceRepository repository,
            EnhancerResourceRelationshipMapper errMapper,
            RabbitTemplate rabbit,
            CoreClient coreClient) {
        this.authUtils = authUtils;
        this.resourceMapper = resourceMapper;
        this.repository = repository;
        this.errMapper = errMapper;
        this.rabbit = rabbit;
        this.coreClient = coreClient;
    }


    @Override
    @Transactional
    public Resource addResource() {
        Resource resource = Resource.prototype(new ResourceDTO());
        resourceMapper.insert(resource);
        return resource;
    }

    @Override
    @Transactional
    public Resource addResourceToEnhancer(Long enhancerId, ResourceDTO dto) {
        if(dto.getCreateBy() == null)
            dto.setCreateBy(StpUtil.getLoginIdAsString());
        Resource resource = Resource.prototype(dto);
        resourceMapper.insert(resource);
        errMapper.insert(EnhancerResourceRel.prototype(enhancerId, resource.getId()));
        return resource;
    }

    @Override
    public Resource getResource(Long resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    @Override
    public Map<String, byte[]> getDataFromResource(Long resourceId) {
        Resource meta = getResource(resourceId);
        Map<String, InputStream> dataMap = repository.load(meta);
        Map<String, byte[]> res = new HashMap<>();
        for(Map.Entry<String, InputStream> data: dataMap.entrySet())
            res.put(data.getKey(), IoUtil.readBytes(data.getValue()));
        return res;
    }

    @Override
    public byte[] getDataFromResource(Long resourceId, String dataName) {
        Resource meta = getResource(resourceId);
        InputStream data = repository.load(meta, dataName);
        return IoUtil.readBytes(data);
    }

    @Override
    public void addDataToResource(Long resourceId, Map<String, byte[]> data) {
        Resource meta = getResource(resourceId);
        repository.save(meta.getCreateBy(), meta.getId(), MapUtil.map(data, (k,v)->IoUtil.toStream(v)));
    }

    @Override
    public void addDataToResource(Long resourceId, String dataName, byte[] data) {
        Resource meta = getResource(resourceId);
        repository.save(meta.getCreateBy(), meta.getId(), dataName, IoUtil.toStream(data));
    }

    @Override
    public Map<String, Boolean> release(Long resourceId, List<String> data) {
        Resource meta = getResource(resourceId);
        authUtils.same(meta.getCreateBy());
        Map<String, Boolean> deleted = new HashMap<>();
        for(String dataName: data)
            deleted.put(dataName, repository.release(meta.getCreateBy() ,resourceId, dataName));
        return deleted;
    }

    @Override
    public void removeResource(Long resourceId) {
        // 先释放该resource的所有资源文件再删除resource
        Long userId = getResource(resourceId).getCreateBy();
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
    public List<Resource> getResourcesOfEnhancer(Long enhancerId) {
        List<Long> resourceIds =
                errMapper.selectByEnhancerId(enhancerId).stream()
                .map(EnhancerResourceRel::getResourceId).toList();
        if(resourceIds.isEmpty()) return new ArrayList<>();
        return resourceMapper.selectBatchIds(resourceIds);
    }

    @Override
    public void connectResourceToEnhancer(Long enhancerId, Long resourceId) {
        EnhancerResourceRel target = new EnhancerResourceRel();
        target.setResourceId(resourceId);
        target.setEnhancerId(enhancerId);
        errMapper.insert(target);
    }

    @Override
    public void disconnectResourceFromEnhancer(Long enhancerId, Long resourceId) {
        LambdaUpdateWrapper<EnhancerResourceRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper
                .eq(EnhancerResourceRel::getResourceId, resourceId)
                .eq(EnhancerResourceRel::getEnhancerId, enhancerId);
        errMapper.delete(wrapper);
    }

    @Override
    public List<IdPair> getEnhancerResourceRels(List<Long> enhancerIds) {
        return enhancerIds.stream()
                .map(errMapper::selectByEnhancerId)
                .flatMap(Collection::stream)
                .map(rel->IdPair.of(rel.getEnhancerId().toString(), rel.getResourceId().toString()))
                .toList();
    }

    @Override
    @Transactional
    public void editTitle(Long resourceId, String title) {
        Resource resource = resourceMapper.selectById(resourceId);
        resource.setTitle(title);
        resourceMapper.updateById(resource);
    }

    @Override
    @Transactional
    public void editType(Long resourceId, String type) {
        Resource resource = resourceMapper.selectById(resourceId);
        resource.setType(type);
        resourceMapper.updateById(resource);
    }

    @Override
    @Transactional
    public void editCreateTime(Long resourceId, String createTime) {
        Resource resource = resourceMapper.selectById(resourceId);
        resource.setCreateTime(TimeUtils.parse(createTime));
        resourceMapper.updateById(resource);
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
