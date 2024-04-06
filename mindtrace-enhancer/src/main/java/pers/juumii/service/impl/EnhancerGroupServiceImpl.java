package pers.juumii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.*;
import pers.juumii.mapper.*;
import pers.juumii.service.EnhancerGroupService;
import pers.juumii.service.ResourceService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class EnhancerGroupServiceImpl implements EnhancerGroupService {

    private final EnhancerGroupMapper groupMapper;
    private final EnhancerGroupRelMapper enhancerGroupRelMapper;
    private final EnhancerGroupKnodeRelMapper groupKnodeRelMapper;
    private final EnhancerGroupResourceRelMapper groupResourceRelMapper;
    private final EnhancerMapper enhancerMapper;
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;

    @Autowired
    public EnhancerGroupServiceImpl(
            EnhancerGroupMapper groupMapper,
            EnhancerGroupRelMapper enhancerGroupRelMapper,
            EnhancerGroupKnodeRelMapper groupKnodeRelMapper,
            EnhancerGroupResourceRelMapper groupResourceRelMapper,
            EnhancerMapper enhancerMapper,
            ResourceMapper resourceMapper, ResourceService resourceService) {
        this.groupMapper = groupMapper;
        this.enhancerGroupRelMapper = enhancerGroupRelMapper;
        this.groupKnodeRelMapper = groupKnodeRelMapper;
        this.groupResourceRelMapper = groupResourceRelMapper;
        this.enhancerMapper = enhancerMapper;
        this.resourceMapper = resourceMapper;
        this.resourceService = resourceService;
    }

    @Override
    public EnhancerGroup getEnhancerGroupById(Long groupId) {
        return groupMapper.selectById(groupId);
    }

    @Override
    public List<Long> getRelatedKnodeIdsByGroupId(Long groupId) {
        LambdaQueryWrapper<EnhancerGroupKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupKnodeRel::getGroupId, groupId);
        return groupKnodeRelMapper.selectList(wrapper).stream()
                .map(EnhancerGroupKnodeRel::getKnodeId).toList();
    }

    @Override
    public List<Long> getRelatedEnhancerIdsByGroupId(Long groupId) {
        LambdaQueryWrapper<EnhancerGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupRel::getGroupId, groupId);
        return enhancerGroupRelMapper.selectList(wrapper).stream()
                .sorted(Comparator.comparingInt(EnhancerGroupRel::getEnhancerIndex))
                .map(EnhancerGroupRel::getEnhancerId).toList();
    }

    @Override
    public List<Long> getRelatedResourceIdsByGroupId(Long groupId) {
        LambdaQueryWrapper<EnhancerGroupResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupResourceRel::getGroupId, groupId);
        return groupResourceRelMapper.selectList(wrapper).stream()
                .sorted(Comparator.comparingInt(EnhancerGroupResourceRel::getResourceIndex))
                .map(EnhancerGroupResourceRel::getResourceId).toList();
    }

    @Override
    public List<Long> getEnhancerGroupIdsByKnodeId(Long knodeId) {
        LambdaQueryWrapper<EnhancerGroupKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupKnodeRel::getKnodeId, knodeId);
        return groupKnodeRelMapper.selectList(wrapper).stream()
                .sorted(Comparator.comparingInt(EnhancerGroupKnodeRel::getGroupIndex))
                .map(EnhancerGroupKnodeRel::getGroupId).toList();
    }

    @Override
    public List<Long> getEnhancerGroupIdsByEnhancerId(Long enhancerId) {
        LambdaQueryWrapper<EnhancerGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupRel::getEnhancerId, enhancerId);
        return enhancerGroupRelMapper.selectList(wrapper).stream()
                .map(EnhancerGroupRel::getGroupId).toList();
    }

    @Override
    public List<EnhancerGroup> getGroupsByKnodeId(Long knodeId) {
        List<Long> groupIds = getEnhancerGroupIdsByKnodeId(knodeId);
        if(groupIds.isEmpty()) return new ArrayList<>();
        return groupMapper.selectBatchIds(groupIds);
    }

    @Override
    public List<Enhancer> getEnhancersByGroupId(Long groupId) {
        List<Long> enhancerIds = getRelatedEnhancerIdsByGroupId(groupId);
        if(enhancerIds.isEmpty()) return new ArrayList<>();
        return enhancerMapper.selectBatchIds(enhancerIds).stream().filter(Objects::nonNull).toList();
    }

    @Override
    public List<Resource> getResourcesByGroupId(Long groupId) {
        List<Long> resourceIds = getRelatedResourceIdsByGroupId(groupId);
        if(resourceIds.isEmpty()) return new ArrayList<>();
        return resourceMapper.selectBatchIds(resourceIds);
    }

    @Override
    @Transactional
    public void addEnhancerGroup(Long userId) {
        groupMapper.insert(EnhancerGroup.prototype(userId));
    }

    @Override
    @Transactional
    public void addEnhancerGroupKnodeRel(Long groupId, Long knodeId) {
        LambdaQueryWrapper<EnhancerGroupKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupKnodeRel::getKnodeId, knodeId)
                .eq(EnhancerGroupKnodeRel::getGroupId, groupId);
        if(groupKnodeRelMapper.exists(wrapper)) return;
        List<Long> rels = getEnhancerGroupIdsByKnodeId(knodeId);
        EnhancerGroupKnodeRel prototype = EnhancerGroupKnodeRel.prototype(groupId, knodeId, rels.size());
        groupKnodeRelMapper.insert(prototype);
    }

    @Override
    @Transactional
    public void addEnhancerGroupRel(Long enhancerId, Long groupId) {
        LambdaQueryWrapper<EnhancerGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupRel::getEnhancerId, enhancerId)
                .eq(EnhancerGroupRel::getGroupId, groupId);
        if(enhancerGroupRelMapper.exists(wrapper)) return;
        List<Long> rels = getRelatedEnhancerIdsByGroupId(groupId);
        EnhancerGroupRel prototype = EnhancerGroupRel.prototype(enhancerId, groupId, rels.size());
        enhancerGroupRelMapper.insert(prototype);
    }

    @Override
    @Transactional
    public void addEnhancerGroupResourceRel(Long groupId, Long resourceId) {
        LambdaQueryWrapper<EnhancerGroupResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerGroupResourceRel::getResourceId, resourceId)
                .eq(EnhancerGroupResourceRel::getGroupId, groupId);
        if(groupResourceRelMapper.exists(wrapper)) return;
        List<Long> rels = getRelatedResourceIdsByGroupId(groupId);
        EnhancerGroupResourceRel prototype = EnhancerGroupResourceRel.prototype(groupId, resourceId, rels.size());
        groupResourceRelMapper.insert(prototype);
    }

    @Override
    @Transactional
    public Resource addResourceToEnhancerGroup(Long groupId, Long userId, String type) {
        Resource resource = resourceService.addResource(userId, type);
        addEnhancerGroupResourceRel(groupId, resource.getId());
        return resource;
    }

    @Override
    @Transactional
    public void removeEnhancerGroup(Long groupId) {
        groupMapper.deleteById(groupId);
        LambdaUpdateWrapper<EnhancerGroupRel> enhancerGroupRelWrapper = new LambdaUpdateWrapper<>();
        enhancerGroupRelWrapper.eq(EnhancerGroupRel::getGroupId, groupId);
        enhancerGroupRelMapper.delete(enhancerGroupRelWrapper);
        LambdaQueryWrapper<EnhancerGroupKnodeRel> groupKnodeRelWrapper = new LambdaQueryWrapper<>();
        groupKnodeRelWrapper.eq(EnhancerGroupKnodeRel::getGroupId, groupId);
        groupKnodeRelMapper.delete(groupKnodeRelWrapper);
        List<Long> resourceIds = getRelatedResourceIdsByGroupId(groupId);
        resourceIds.forEach(resourceService::removeResource);
        LambdaUpdateWrapper<EnhancerGroupResourceRel> groupResourceRelWrapper = new LambdaUpdateWrapper<>();
        groupResourceRelWrapper.eq(EnhancerGroupResourceRel::getGroupId, groupId);
        groupResourceRelMapper.delete(groupResourceRelWrapper);
    }

    @Override
    @Transactional
    public void removeEnhancerGroupRel(Long enhancerId, Long groupId) {
        LambdaUpdateWrapper<EnhancerGroupRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerGroupRel::getGroupId, groupId)
                .eq(EnhancerGroupRel::getEnhancerId, enhancerId);
        enhancerGroupRelMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void removeEnhancerGroupKnodeRel(Long groupId, Long knodeId) {
        LambdaUpdateWrapper<EnhancerGroupKnodeRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerGroupKnodeRel::getGroupId, groupId)
                .eq(EnhancerGroupKnodeRel::getKnodeId, knodeId);
        groupKnodeRelMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void removeEnhancerGroupResourceRel(Long groupId, Long resourceId) {
        LambdaUpdateWrapper<EnhancerGroupResourceRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerGroupResourceRel::getGroupId, groupId)
                .eq(EnhancerGroupResourceRel::getResourceId, resourceId);
        groupResourceRelMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void removeResourceFromEnhancerGroup(Long groupId, Long resourceId) {
        resourceService.removeResource(resourceId);
        removeEnhancerGroupResourceRel(groupId, resourceId);
    }

    @Override
    @Transactional
    public void setEnhancerGroupTitle(Long groupId, String title) {
        EnhancerGroup group = groupMapper.selectById(groupId);
        group.setTitle(title);
        groupMapper.updateById(group);
    }

    @Override
    @Transactional
    public void addEnhancerGroupToKnode(Long userId, Long knodeId) {
        EnhancerGroup prototype = EnhancerGroup.prototype(userId);
        groupMapper.insert(prototype);
        addEnhancerGroupKnodeRel(prototype.getId(), knodeId);
    }
}
