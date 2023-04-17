package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.dto.LearningTraceDTO;
import pers.juumii.dto.TraceInfo;
import pers.juumii.mapper.LearningTraceMapper;
import pers.juumii.mapper.MindtraceMapper;
import pers.juumii.service.LearningTraceService;

@RestController
@RequestMapping("/user/{userId}/learn")
public class TracingController {

    private final ControllerAspect aspect;
    private final LearningTraceService learningTraceService;

    @Autowired
    public TracingController(
            ControllerAspect aspect,
            LearningTraceService learningTraceService) {
        this.aspect = aspect;
        this.learningTraceService = learningTraceService;
    }

    @GetMapping("/now")
    public Object checkNow(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return LearningTraceDTO.transfer(learningTraceService.checkNow(userId));
    }

    @GetMapping
    public Object checkAll(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return LearningTraceDTO.transfer(learningTraceService.checkAll(userId));
    }

    @GetMapping("/knode/{knodeId}")
    public Object checkKnodeRelatedLearningTraces(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return LearningTraceDTO.transfer(learningTraceService.checkKnodeRelatedLearningTraces(userId, knodeId));
    }

    @GetMapping("/latest")
    public Object checkLatest(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return LearningTraceDTO.transfer(learningTraceService.checkLatest(userId));
    }

    @GetMapping("/trace/{learningTraceId}/knode")
    public Object getRelatedKnodeIdsOfLearningTrace(
            @PathVariable Long userId,
            @PathVariable Long learningTraceId){
        aspect.checkTraceAvailability(userId, learningTraceId);
        return learningTraceService.getRelatedKnodeIdsOfLearningTrace(learningTraceId)
                .stream().map(Object::toString).toList();
    }

    @PostMapping
    public Object postLearningState(
            @PathVariable Long userId,
            @RequestBody TraceInfo traceInfo){
        aspect.checkUserExistence(userId);
        return switch (traceInfo.getType()){
            case TraceInfo.START_LEARNING -> learningTraceService.startLearning(userId, traceInfo);
            case TraceInfo.FINISH_LEARNING -> learningTraceService.finishLearning(userId, traceInfo);
            case TraceInfo.PAUSE_LEARNING -> LearningTraceDTO.transfer(learningTraceService.pauseLearning(userId, traceInfo));
            case TraceInfo.CONTINUE_LEARNING -> LearningTraceDTO.transfer(learningTraceService.continueLearning(userId, traceInfo));
            case TraceInfo.SETTLE_LEARNING -> learningTraceService.settleLearning(userId, traceInfo);
            default -> SaResult.error("Wrong Trace Info Type: " + traceInfo.getType());
        };
    }

    @DeleteMapping("/trace/{traceId}")
    public Object removeLearningTrace(
            @PathVariable Long userId,
            @PathVariable Long traceId){
        aspect.checkTraceAvailability(userId, traceId);
        return learningTraceService.removeLearningTraceById(traceId);
    }
}
