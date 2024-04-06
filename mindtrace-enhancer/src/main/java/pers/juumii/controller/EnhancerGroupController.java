package pers.juumii.controller;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerGroup;
import pers.juumii.data.Resource;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.enhancer.EnhancerGroupDTO;
import pers.juumii.dto.enhancer.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.EnhancerGroupService;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.AuthUtils;

import java.util.List;

@RestController
public class EnhancerGroupController {

    private final EnhancerGroupService groupService;
    private final EnhancerService enhancerService;
    private final ResourceService resourceService;
    private final AuthUtils authUtils;
    private final CoreClient coreClient;

    @Autowired
    public EnhancerGroupController(
            EnhancerService enhancerService,
            EnhancerGroupService groupService,
            ResourceService resourceService,
            AuthUtils authUtils,
            CoreClient coreClient) {
        this.enhancerService = enhancerService;
        this.groupService = groupService;
        this.resourceService = resourceService;
        this.authUtils = authUtils;
        this.coreClient = coreClient;
    }

    private void groupSameUser(Long groupId){
        EnhancerGroup group = groupService.getEnhancerGroupById(groupId);
        authUtils.same(group.getUserId());
    }

    private void enhancerSameUser(Long enhancerId){
        Enhancer enhancer = enhancerService.getEnhancerById(enhancerId);
        authUtils.same(enhancer.getCreateBy());
    }

    private void knodeSameUser(Long knodeId){
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
    }

    private void resourceSameUser(Long resourceId){
        Resource resource = resourceService.getResource(resourceId);
        authUtils.same(Convert.toLong(resource.getCreateBy()));
    }

    @GetMapping("/enhancer-group/{groupId}")
    public EnhancerGroupDTO getEnhancerGroupById(@PathVariable Long groupId){
        return EnhancerGroup.transfer(groupService.getEnhancerGroupById(groupId));
    }

    @GetMapping("/rel/enhancer-group/{groupId}/knode/id")
    public List<String> getRelatedKnodeIdsByGroupId(@PathVariable Long groupId){
        return groupService.getRelatedKnodeIdsByGroupId(groupId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/rel/enhancer-group/{groupId}/enhancer/id")
    public List<String> getRelatedEnhancerIdsByGroupId(@PathVariable Long groupId){
        return groupService.getRelatedEnhancerIdsByGroupId(groupId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/rel/enhancer-group/{groupId}/resource/id")
    public List<String> getRelatedResourceIdsByGroupId(@PathVariable Long groupId){
        return groupService.getRelatedResourceIdsByGroupId(groupId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/rel/knode/{knodeId}/enhancer-group/id")
    private List<String> getEnhancerGroupIdsByKnodeId(@PathVariable Long knodeId){
        return groupService.getEnhancerGroupIdsByKnodeId(knodeId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/rel/enhancer/{enhancerId}/enhancer-group/id")
    public List<String> getEnhancerGroupIdsByEnhancerId(@PathVariable Long enhancerId){
        return groupService.getEnhancerGroupIdsByEnhancerId(enhancerId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/rel/knode/{knodeId}/enhancer-group")
    public List<EnhancerGroupDTO> getGroupsByKnodeId(@PathVariable Long knodeId){
        return EnhancerGroup.transfer(groupService.getGroupsByKnodeId(knodeId));
    }

    @GetMapping("/rel/enhancer-group/{groupId}/enhancer")
    public List<EnhancerDTO> getEnhancersByGroupId(@PathVariable Long groupId){
        return Enhancer.transfer(groupService.getEnhancersByGroupId(groupId));
    }

    @GetMapping("/rel/enhancer-group/{groupId}/resource")
    public List<ResourceDTO> getResourcesByGroupId(@PathVariable Long groupId){
        return Resource.transfer(groupService.getResourcesByGroupId(groupId));
    }

    @PutMapping("/user/{userId}/enhancer-group")
    public void addEnhancerGroup(@PathVariable Long userId){
        authUtils.same(userId);
        groupService.addEnhancerGroup(userId);
    }

    @PutMapping("/rel/enhancer-group/knode")
    public void addEnhancerGroupKnodeRel(@RequestParam Long groupId, @RequestParam Long knodeId){
        groupSameUser(groupId);
        knodeSameUser(knodeId);
        groupService.addEnhancerGroupKnodeRel(groupId, knodeId);
    }

    @PutMapping("/rel/enhancer-group/enhancer")
    public void addEnhancerGroupRel(@RequestParam Long enhancerId, @RequestParam Long groupId){
        enhancerSameUser(enhancerId);
        groupSameUser(groupId);
        groupService.addEnhancerGroupRel(enhancerId, groupId);
    }

    @PutMapping("/rel/enhancer-group/resource")
    void addEnhancerGroupResourceRel(@RequestParam Long groupId, @RequestParam Long resourceId){
        groupSameUser(groupId);
        resourceSameUser(resourceId);
        groupService.addEnhancerGroupResourceRel(groupId, resourceId);
    }

    @PutMapping("/user/{userId}/knode/{knodeId}/enhancer-group")
    public void addEnhancerGroupToKnode(@PathVariable Long userId, @PathVariable Long knodeId){
        knodeSameUser(knodeId);
        groupService.addEnhancerGroupToKnode(userId, knodeId);
    }

    @PutMapping("/enhancer-group/{groupId}/resource")
    public ResourceDTO addResourceToEnhancerGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam String type){
        groupSameUser(groupId);
        authUtils.same(userId);
        return Resource.transfer(groupService.addResourceToEnhancerGroup(groupId, userId, type));
    }

    @DeleteMapping("/enhancer-group/{groupId}")
    public void removeEnhancerGroup(@PathVariable Long groupId){
        groupSameUser(groupId);
        groupService.removeEnhancerGroup(groupId);
    }

    @DeleteMapping("/rel/enhancer-group/{groupId}/enhancerId/{enhancerId}")
    public void removeEnhancerGroupRel(@PathVariable Long enhancerId, @PathVariable Long groupId){
        enhancerSameUser(enhancerId);
        groupSameUser(groupId);
        groupService.removeEnhancerGroupRel(enhancerId, groupId);
    }

    @DeleteMapping("/rel/enhancer-group/{groupId}/knode/{knodeId}")
    public void removeEnhancerGroupKnodeRel(@PathVariable Long groupId, @PathVariable Long knodeId){
        groupSameUser(groupId);
        knodeSameUser(knodeId);
        groupService.removeEnhancerGroupKnodeRel(groupId, knodeId);
    }


    @DeleteMapping("/rel/enhancer-group/{groupId}/resource/{resourceId}")
    public void removeEnhancerGroupResourceRel(@PathVariable Long groupId, @PathVariable Long resourceId){
        groupSameUser(groupId);
        resourceSameUser(resourceId);
        groupService.removeEnhancerGroupResourceRel(groupId, resourceId);
    }

    @DeleteMapping("/enhancer-group/{groupId}/resource/{resourceId}")
    public void removeResourceFromEnhancerGroup(@PathVariable Long groupId, @PathVariable Long resourceId){
        groupSameUser(groupId);
        resourceSameUser(resourceId);
        groupService.removeResourceFromEnhancerGroup(groupId, resourceId);
    }

    @PutMapping("/enhancer-group/{groupId}/title")
    public void setEnhancerGroupTitle(@PathVariable Long groupId, @RequestParam String title){
        groupSameUser(groupId);
        groupService.setEnhancerGroupTitle(groupId, title);
    }




}
