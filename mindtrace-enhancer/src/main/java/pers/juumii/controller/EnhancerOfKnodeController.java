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
        return enhancerService.getEnhancersFromKnode(knodeId);
    }

    @PutMapping
    public Object addEnhancerToKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.addEnhancerToKnode(userId, knodeId);
    }

    @PutMapping("/quizcard")
    public Object addEnhancerWithQuizcardToKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.addEnhancerWithQuizcardToKnode(userId, knodeId);
    }

    @PostMapping("/{enhancerId}")
    public Object connectEnhancerToKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @PathVariable Long enhancerId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.connectEnhancerToKnode(userId, knodeId, enhancerId);
    }

    @DeleteMapping("/{enhancerId}")
    public Object disconnectEnhancerToKnode(
            @PathVariable Long userId,
            @PathVariable Long enhancerId,
            @PathVariable Long knodeId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        aspect.checkKnodeAvailability(userId, knodeId);
        return enhancerService.disconnectEnhancerToKnode(userId,knodeId,enhancerId);
    }
}
