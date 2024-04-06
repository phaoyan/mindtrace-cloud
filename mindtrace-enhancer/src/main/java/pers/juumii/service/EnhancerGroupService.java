package pers.juumii.service;

import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerGroup;
import pers.juumii.data.Resource;

import java.util.List;

public interface EnhancerGroupService {

    EnhancerGroup getEnhancerGroupById(Long groupId);

    List<Long> getRelatedKnodeIdsByGroupId(Long groupId);

    List<Long> getRelatedEnhancerIdsByGroupId(Long groupId);

    List<Long> getRelatedResourceIdsByGroupId(Long groupId);

    List<Long> getEnhancerGroupIdsByKnodeId(Long knodeId);

    List<Long> getEnhancerGroupIdsByEnhancerId(Long enhancerId);

    List<EnhancerGroup> getGroupsByKnodeId(Long knodeId);

    List<Enhancer> getEnhancersByGroupId(Long groupId);

    List<Resource> getResourcesByGroupId(Long groupId);

    void addEnhancerGroup(Long userId);

    void addEnhancerGroupKnodeRel(Long groupId, Long knodeId);

    void addEnhancerGroupRel(Long enhancerId, Long groupId);

    void addEnhancerGroupResourceRel(Long groupId, Long resourceId);

    Resource addResourceToEnhancerGroup(Long groupId, Long userId, String type);

    void removeEnhancerGroup(Long groupId);

    void removeEnhancerGroupRel(Long enhancerId, Long groupId);

    void removeEnhancerGroupKnodeRel(Long groupId, Long knodeId);

    void removeEnhancerGroupResourceRel(Long groupId, Long resourceId);

    void removeResourceFromEnhancerGroup(Long groupId, Long resourceId);

    void setEnhancerGroupTitle(Long groupId, String title);

    void addEnhancerGroupToKnode(Long userId, Long knodeId);

}
