package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.EnhancerResourceRelationshipMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.TimeUtils;

import java.io.InputStream;
import java.util.*;

@Service
public class ResourceServiceImpl implements ResourceService {


    private final ResourceMapper resourceMapper;
    private final ResourceRepository repository;
    private final EnhancerResourceRelationshipMapper errMapper;
    private final CoreClient coreClient;
    private final MqClient mqClient;
    private EnhancerService enhancerService;

    @Lazy
    @Autowired
    public void setEnhancerService(EnhancerService enhancerService) {
        this.enhancerService = enhancerService;
    }

    @Autowired
    public ResourceServiceImpl(
            ResourceMapper resourceMapper,
            ResourceRepository repository,
            EnhancerResourceRelationshipMapper errMapper,
            CoreClient coreClient,
            MqClient mqClient) {
        this.resourceMapper = resourceMapper;
        this.repository = repository;
        this.errMapper = errMapper;
        this.coreClient = coreClient;
        this.mqClient = mqClient;
    }


    @Override
    @Transactional
    public Resource addResource(Long userId, String type) {
        Resource resource = Resource.prototype(userId);
        resource.setType(type);
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
        errMapper.insert(EnhancerResourceRel.prototype(enhancerId, resource.getId(), getResourcesOfEnhancer(enhancerId).size()));
        return resource;
    }

    @Override
    public Resource getResource(Long resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    @Override
    public Map<String, byte[]> getDataFromResource(Long resourceId) {
        Resource meta = getResource(resourceId);
        if(meta == null) return new HashMap<>();
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
        Map<String, Boolean> deleted = new HashMap<>();
        for(String dataName: data)
            deleted.put(dataName, repository.release(meta.getCreateBy() ,resourceId, dataName));
        return deleted;
    }

    @Override
    public void removeResource(Long resourceId) {
        // 先释放该resource的所有资源文件再删除resource
        Long userId = getResource(resourceId).getCreateBy();
        repository.releaseAll(userId, resourceId);
        resourceMapper.deleteById(resourceId);
        errMapper.deleteByResourceId(resourceId);
        mqClient.emit(MessageEvents.REMOVE_RESOURCE, resourceId.toString());
    }

    @Override
    public void removeAllResourcesFromEnhancer(Long enhancerId) {
        for(Resource resource: getResourcesOfEnhancer(enhancerId))
            removeResource(resource.getId());
    }

    @Override
    public List<Resource> getResourcesOfEnhancer(Long enhancerId) {
        List<Long> resourceIds =
                errMapper.selectByEnhancerId(enhancerId).stream()
                .sorted(Comparator.comparingInt(EnhancerResourceRel::getResourceIndex))
                .map(EnhancerResourceRel::getResourceId).toList();
        return resourceIds.isEmpty() ?
                new ArrayList<>() :
                resourceMapper.selectBatchIds(resourceIds);
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
    @Transactional
    public void setResourceIndexInEnhancer(Long enhancerId, Long resourceId, Integer index) {
        if(index < 0)
            throw new RuntimeException("Wrong Index : " + index);
        correctResourceIndexInEnhancer(enhancerId);
        LambdaQueryWrapper<EnhancerResourceRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(EnhancerResourceRel::getEnhancerId, enhancerId)
                .eq(EnhancerResourceRel::getResourceId, resourceId);
        EnhancerResourceRel rel = errMapper.selectOne(queryWrapper);
        LambdaUpdateWrapper<EnhancerResourceRel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(EnhancerResourceRel::getEnhancerId, enhancerId)
                .eq(EnhancerResourceRel::getResourceId, resourceId);
        errMapper.update(rel, updateWrapper);
    }

    public void correctResourceIndexInEnhancer(Long enhancerId){
        List<EnhancerResourceRel> rels = errMapper.selectByEnhancerId(enhancerId);
        rels.sort(Comparator.comparingInt(EnhancerResourceRel::getResourceIndex));
        for(int i = 1; i < rels.size(); i ++){
            EnhancerResourceRel cur = rels.get(i);
            cur.setResourceIndex(i);
            LambdaUpdateWrapper<EnhancerResourceRel> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .eq(EnhancerResourceRel::getResourceId, cur.getResourceId())
                    .eq(EnhancerResourceRel::getEnhancerId, cur.getEnhancerId());
            errMapper.update(cur, updateWrapper);
        }
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
