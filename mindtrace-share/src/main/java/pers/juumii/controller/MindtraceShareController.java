package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.ResourceInfoDTO;
import pers.juumii.service.EnhancerShareService;
import pers.juumii.service.KnodeShareService;
import pers.juumii.service.QueryService;
import pers.juumii.service.ResourceShareService;

@RestController
@RequestMapping("/user/{userId}")
public class MindtraceShareController {

    private final ResourceShareService resourceShareService;
    private final EnhancerShareService enhancerShareService;
    private final KnodeShareService knodeShareService;
    private final QueryService queryService;

    @Autowired
    public MindtraceShareController(
            ResourceShareService resourceShareService,
            EnhancerShareService enhancerShareService,
            KnodeShareService knodeShareService,
            QueryService queryService) {
        this.resourceShareService = resourceShareService;
        this.enhancerShareService = enhancerShareService;
        this.knodeShareService = knodeShareService;
        this.queryService = queryService;
    }

    @GetMapping("/knode/{knodeId}/resource")
    public Object getRelatedResourceShares(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return queryService.getRelatedResourceShares(userId, knodeId);
    }

    @GetMapping("/knode/{knodeId}/enhancer")
    public Object getRelatedEnhancerShares(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return queryService.getRelatedEnhancerShares(userId, knodeId);
    }

    @GetMapping("/knode/{knodeId}")
    public Object getRelatedKnodeShares(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return queryService.getRelatedKnodeShares(userId, knodeId);
    }

    // 更新一个resource的share信息，包括评分、使用率等
    @PostMapping("/resource/{resourceId}/info")
    public Object updateResourceInfo(
            @PathVariable Long userId,
            @PathVariable Long resourceId,
            @RequestBody ResourceInfoDTO dto){
        return resourceShareService.updateResourceInfo(userId, resourceId, dto);
    }

    // 分享一个knode，即将其记录在knode_share数据库中
    @PostMapping("/knode/{knodeId}")
    public Object shareKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeShareService.shareKnode(userId, knodeId);
    }

    // 分享一个enhancer，即将其记录在enhancer_share数据库中
    @PostMapping("/enhancer/{enhancerId}")
    public Object shareEnhancer(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        return enhancerShareService.shareEnhancer(userId, enhancerId);
    }

    @PostMapping("/resource/{resourceId}")
    public Object shareResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        return resourceShareService.shareResource(userId, resourceId);
    }

    // 取消对一个knode的分享，即将其记录从knode_share数据库中删除
    @DeleteMapping("/knode/{knodeId}")
    public Object hideKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeShareService.hideKnode(userId, knodeId);
    }

    // 取消对一个enhancer的分享，即将其记录从enhancer_share数据库中删除
    @DeleteMapping("/enhancer/{enhancerId}")
    public Object hideEnhancer(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        return enhancerShareService.hideEnhancer(userId, enhancerId);
    }

    @DeleteMapping("/resource/{resourceId}")
    public Object hideResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        return resourceShareService.hideResource(userId, resourceId);
    }

}
