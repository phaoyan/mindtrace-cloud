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

    private final SamplingService samplingService;

    @Autowired
    public SamplingController(SamplingService samplingService) {
        this.samplingService = samplingService;
    }

    @GetMapping("/knode/{knodeId}/trace")
    public List<MindtraceDTO> getKnodeTrace(
            @PathVariable Long userId,
            @PathVariable Long knodeId) {
        return MindtraceDTO.transfer(samplingService.knodeTrace(userId, knodeId));
    }

    @GetMapping("/enhancer/{enhancerId}/trace")
    public List<MindtraceDTO> getEnhancerTrace(
            @PathVariable Long userId,
            @PathVariable Long enhancerId) {
        return MindtraceDTO.transfer(samplingService.enhancerTrace(userId, enhancerId));
    }

    @GetMapping("/knode/label/{labelName}/trace")
    public List<MindtraceDTO> getKnodeFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        return MindtraceDTO.transfer(samplingService.knodeFeatureTrace(userId, labelName));
    }

    @GetMapping("/enhancer/label/{labelName}/trace")
    public List<MindtraceDTO> getEnhancerFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String labelName) {
        return MindtraceDTO.transfer(samplingService.enhancerFeatureTrace(userId, labelName));
    }

    @GetMapping("/matched/{knodeLabel}/{enhancerLabel}/trace")
    public List<MindtraceDTO> getMatchedFeatureTrace(
            @PathVariable Long userId,
            @PathVariable String knodeLabel,
            @PathVariable String enhancerLabel) {
        return MindtraceDTO.transfer(samplingService.matchedFeatureTrace(userId, knodeLabel, enhancerLabel));
    }

}

