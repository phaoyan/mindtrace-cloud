package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.dto.LearningTraceDTO;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.dto.TraceInfo;
import pers.juumii.mapper.LearningTraceMapper;
import pers.juumii.mapper.MindtraceMapper;
import pers.juumii.service.LearningTraceService;

import java.util.List;

@RestController
public class TracingController {

    private final LearningTraceService learningTraceService;

    @Autowired
    public TracingController(LearningTraceService learningTraceService) {
        this.learningTraceService = learningTraceService;
    }

    @GetMapping("/user/{userId}/learn/now")
    public LearningTraceDTO checkNow(@PathVariable Long userId){
        return LearningTraceDTO.transfer(learningTraceService.checkNow(userId));
    }

    @GetMapping("/user/{userId}/learn")
    public List<LearningTraceDTO> checkAll(@PathVariable Long userId){
        return LearningTraceDTO.transfer(learningTraceService.checkAll(userId));
    }

    @GetMapping("/user/{userId}/learn/knode/{knodeId}")
    public List<LearningTraceDTO> checkKnodeRelatedLearningTraces(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return LearningTraceDTO.transfer(learningTraceService.checkKnodeRelatedLearningTraces(userId, knodeId));
    }

    @GetMapping("/user/{userId}/learn/latest")
    public LearningTraceDTO checkLatest(@PathVariable Long userId){
        return LearningTraceDTO.transfer(learningTraceService.checkLatest(userId));
    }

    @GetMapping("/user/{userId}/learn/trace/{learningTraceId}/knode")
    public List<String> getRelatedKnodeIdsOfLearningTrace(
            @PathVariable Long userId,
            @PathVariable Long learningTraceId){
        return learningTraceService.getRelatedKnodeIdsOfLearningTrace(learningTraceId)
                .stream().map(Object::toString).toList();
    }

    @DeleteMapping("/user/{userId}/learn/trace/{traceId}")
    public SaResult removeLearningTrace(
            @PathVariable Long userId,
            @PathVariable Long traceId){
        return learningTraceService.removeLearningTraceById(traceId);
    }

    @PostMapping("/user/{userId}/learn")
    public Object postLearningState(
            @PathVariable Long userId,
            @RequestBody TraceInfo traceInfo){
        return switch (traceInfo.getType()){
            case TraceInfo.START_LEARNING -> LearningTraceDTO.transfer(learningTraceService.startLearning(userId, traceInfo));
            case TraceInfo.FINISH_LEARNING -> learningTraceService.finishLearning(userId, traceInfo);
            case TraceInfo.PAUSE_LEARNING -> LearningTraceDTO.transfer(learningTraceService.pauseLearning(userId, traceInfo));
            case TraceInfo.CONTINUE_LEARNING -> LearningTraceDTO.transfer(learningTraceService.continueLearning(userId, traceInfo));
            case TraceInfo.SETTLE_LEARNING -> MindtraceDTO.transfer(learningTraceService.settleLearning(userId, traceInfo));
            case TraceInfo.DROP_LEARNING -> learningTraceService.dropLearning(userId, traceInfo);
            default -> SaResult.error("Wrong Trace Info Type: " + traceInfo.getType());
        };
    }


}
