package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Mindtrace;
import pers.juumii.service.SamplingService;

import java.util.List;

@RestController
@RequestMapping("/trace/user/{userId}")
public class SamplingController {
    private final SamplingService samplingService;

    @Autowired
    public SamplingController(SamplingService samplingService) {
        this.samplingService = samplingService;
    }

    @GetMapping("/knode/{knodeId}")
    public List<Mindtrace> getKnodeTrace(
            @PathVariable Long userId,
            @PathVariable Long knodeId) {
        return samplingService.knodeTrace(userId, knodeId);
    }

    @GetMapping("/enhancer/{enhancerId}")
    public List<Mindtrace> getEnhancerTrace(
            @PathVariable Long userId,
            @PathVariable Long enhancerId) {
        return samplingService.enhancerTrace(userId, enhancerId);
    }

    @GetMapping("/knode/label/{labelName}")
    public List<Mindtrace> getKnodeFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        return samplingService.knodeFeatureTrace(userId, labelName);
    }

    @GetMapping("/enhancer/label/{labelName}")
    public List<Mindtrace> getEnhancerFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        return samplingService.enhancerFeatureTrace(userId, labelName);
    }

    @GetMapping("/matched/{knodeLabel}/{enhancerLabel}")
    public List<Mindtrace> getMatchedFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String knodeLabel,
            @PathVariable String enhancerLabel) {
        return samplingService.matchedFeatureTrace(userId, knodeLabel, enhancerLabel);
    }

}

