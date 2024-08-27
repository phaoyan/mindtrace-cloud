package pers.juumii.controller;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.service.StudyTraceService;
import pers.juumii.utils.AuthUtils;

import java.util.Comparator;
import java.util.List;

@RestController
public class StudyTraceController {

    private final StudyTraceService studyTraceService;
    private final AuthUtils authUtils;

    private void traceSameUser(Long traceId){
        StudyTrace studyTrace = studyTraceService.getStudyTrace(traceId);
        authUtils.same(studyTrace.getUserId());
    }

    @Autowired
    public StudyTraceController(StudyTraceService studyTraceService, AuthUtils authUtils) {
        this.studyTraceService = studyTraceService;
        this.authUtils = authUtils;
    }

    @PutMapping("/study/trace")
    public StudyTraceDTO addStudyTrace(@RequestBody StudyTraceDTO data){
        return StudyTrace.transfer(studyTraceService.addStudyTrace(data));
    }

    @GetMapping("/study/trace")
    public List<StudyTraceDTO> getUserStudyTraces(
            @RequestParam(value = "userId", required = false) Long userId){
        return StudyTrace.transfer(studyTraceService.getUserStudyTraces(userId));
    }

    @GetMapping("/study/trace/{traceId}")
    public StudyTraceDTO getStudyTrace(@PathVariable Long traceId){
        return StudyTrace.transfer(studyTraceService.getStudyTrace(traceId));
    }

    @DeleteMapping("/study/trace/{traceId}")
    public void removeStudyTrace(@PathVariable Long traceId){
        traceSameUser(traceId);
        studyTraceService.removeStudyTrace(traceId);
    }

    @PostMapping("/study/trace")
    public void updateTraceTitle(@RequestBody StudyTraceDTO trace){
        traceSameUser(Convert.toLong(trace.getId()));
        studyTraceService.updateStudyTrace(trace);
    }

    @GetMapping("/study/trace/{traceId}/enhancer")
    public List<String> getEnhancerIdsByTraceId(@PathVariable Long traceId){
        return studyTraceService.getEnhancerIdsByTraceId(traceId).stream().map(Object::toString).toList();
    }

    @GetMapping("/study/trace/{traceId}/knode")
    public List<String> getKnodeIdsByTraceId(@PathVariable Long traceId){
        return studyTraceService.getKnodeIdsByTraceId(traceId).stream().map(Object::toString).toList();
    }

    @GetMapping("/study/knode/{knodeId}/trace")
    public List<StudyTraceDTO> getStudyTracesOfKnode(@PathVariable Long knodeId){
        return StudyTrace.transfer(studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId));
    }

    @GetMapping("/page/study/knode/{knodeId}/trace")
    public List<StudyTraceDTO> getStudyTraceOfKnodeByPage(
            @PathVariable Long knodeId,
            @RequestParam Integer page,
            @RequestParam Integer size){
        List<StudyTrace> traces =
                studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId)
                .stream().sorted(Comparator.comparing(StudyTrace::getStartTime).reversed())
                .toList();
        return StudyTrace.transfer(traces.subList(Math.min(page * size, traces.size()), Math.min(traces.size(), (page + 1) * size)));
    }

    @PostMapping("/batch/study/knode/trace")
    public List<StudyTraceDTO> getStudyTracesOfKnodeBatch(@RequestBody List<Long> knodeIds){
        return StudyTrace.transfer(studyTraceService.getStudyTracesOfKnodeBatch(knodeIds));
    }

    @PostMapping("/rel/trace/enhancer")
    public List<IdPair> getEnhancerIdsByTraceId(@RequestBody List<Long> traceIds){
        return studyTraceService.getEnhancerIdsByTraceId(traceIds);
    }

    @PutMapping("/rel/trace/enhancer")
    public void addTraceEnhancerRel(@RequestParam Long traceId, @RequestParam Long enhancerId){
        traceSameUser(traceId);
        studyTraceService.addTraceEnhancerRel(traceId, enhancerId);
    }

    @DeleteMapping("/rel/trace/enhancer")
    public void removeTraceEnhancerRel(@RequestParam Long traceId, @RequestParam Long enhancerId){
        traceSameUser(traceId);
        studyTraceService.removeTraceEnhancerRel(traceId, enhancerId);
    }


}
