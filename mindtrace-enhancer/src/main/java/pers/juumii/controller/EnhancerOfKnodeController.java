package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.aop.ControllerAspect;
import pers.juumii.service.EnhancerService;

@RestController
@RequestMapping("user/{userId}/knode/{knodeId}/enhancer")
public class EnhancerOfKnodeController {


    private final ControllerAspect aspect;
    private final EnhancerService enhancerService;

    @Autowired
    public EnhancerOfKnodeController(ControllerAspect aspect, EnhancerService enhancerService) {
        this.aspect = aspect;
        this.enhancerService = enhancerService;
    }

    @GetMapping
    public Object getEnhancerFromKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.queryByKnodeId(knodeId);
    }

    @PostMapping("/{enhancerId}")
    public Object connectEnhancerToKnode(
            @PathVariable Long userId,
            @PathVariable Long enhancerId,
            @PathVariable Long knodeId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.connectEnhancerToKnode(userId, enhancerId, knodeId);
    }

    @DeleteMapping("/{enhancerId}")
    public Object disconnectEnhancerToKnode(
            @PathVariable Long userId,
            @PathVariable Long enhancerId,
            @PathVariable Long knodeId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.disconnectEnhancerToKnode(userId,enhancerId,knodeId);
    }
}
