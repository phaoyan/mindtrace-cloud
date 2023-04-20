package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.data.Mindtrace;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.service.SamplingService;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/mind")
public class SamplingController {

    private final ControllerAspect aspect;
    private final SamplingService samplingService;

    @Autowired
    public SamplingController(ControllerAspect aspect, SamplingService samplingService) {
        this.aspect = aspect;
        this.samplingService = samplingService;
    }

    @GetMapping("/knode/{knodeId}/trace")
    public Object getKnodeTrace(
            @PathVariable Long userId,
            @PathVariable Long knodeId) {
        aspect.checkKnodeAvailability(userId, knodeId);
        return MindtraceDTO.transfer(samplingService.knodeTrace(userId, knodeId));
    }

    @GetMapping("/enhancer/{enhancerId}/trace")
    public Object getEnhancerTrace(
            @PathVariable Long userId,
            @PathVariable Long enhancerId) {
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return MindtraceDTO.transfer(samplingService.enhancerTrace(userId, enhancerId));
    }

    @GetMapping("/knode/label/{labelName}/trace")
    public Object getKnodeFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        aspect.checkUserExistence(userId);
        return MindtraceDTO.transfer(samplingService.knodeFeatureTrace(userId, labelName));
    }

    @GetMapping("/enhancer/label/{labelName}/trace")
    public Object getEnhancerFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        aspect.checkUserExistence(userId);
        return MindtraceDTO.transfer(samplingService.enhancerFeatureTrace(userId, labelName));
    }

    @GetMapping("/matched/{knodeLabel}/{enhancerLabel}/trace")
    public Object getMatchedFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String knodeLabel,
            @PathVariable String enhancerLabel) {
        aspect.checkUserExistence(userId);
        return MindtraceDTO.transfer(samplingService.matchedFeatureTrace(userId, knodeLabel, enhancerLabel));
    }

}

