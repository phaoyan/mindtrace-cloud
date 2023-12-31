package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.MilestoneTraceRel;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.service.StudyTraceService;

import java.util.List;

@RestController
public class StudyTraceController {

    private final StudyTraceService studyTraceService;

    @Autowired
    public StudyTraceController(StudyTraceService studyTraceService) {
        this.studyTraceService = studyTraceService;
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
        studyTraceService.removeStudyTrace(traceId);
    }

    @PostMapping("/study/trace/{traceId}/knode/{knodeId}")
    public void postTraceCoverage(@PathVariable Long traceId, @PathVariable Long knodeId){
        studyTraceService.postTraceCoverage(traceId, knodeId);
    }

    @PostMapping("/study/trace")
    public void updateTraceTitle(@RequestBody StudyTraceDTO trace){
        studyTraceService.updateStudyTrace(trace);
    }


    @GetMapping("/study/trace/{traceId}/knode")
    public List<String> getTraceKnodeRels(@PathVariable Long traceId){
        return studyTraceService.getTraceKnodeRels(traceId).stream().map(Object::toString).toList();
    }

    @GetMapping("/study/trace/{traceId}/enhancer")
    public List<String> getTraceEnhancerRels(@PathVariable Long traceId){
        return studyTraceService.getTraceEnhancerRels(traceId).stream().map(Object::toString).toList();
    }

    @GetMapping("study/trace/{traceId}/knode/{knodeId}")
    public Boolean checkTraceKnodeRel(@PathVariable Long traceId, @PathVariable Long knodeId){
        return studyTraceService.checkTraceKnodeRel(traceId, knodeId);
    }

    @DeleteMapping("study/trace/{traceId}/knode/{knodeId}")
    public void removeTraceCoverage(@PathVariable Long traceId, @PathVariable Long knodeId){
        studyTraceService.removeTraceCoverage(traceId, knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/trace")
    public List<StudyTraceDTO> getStudyTracesOfKnode(@PathVariable Long knodeId){
        return StudyTrace.transfer(studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId));
    }

    @PostMapping("/batch/study/knode/trace")
    public List<StudyTraceDTO> getStudyTracesOfKnodeBatch(@RequestBody List<Long> knodeIds){
        return StudyTrace.transfer(studyTraceService.getStudyTracesOfKnodeBatch(knodeIds));
    }

    @PostMapping("/rel/trace/knode")
    public List<IdPair> getTraceKnodeRels(@RequestBody List<Long> traceIds){
        return studyTraceService.getTraceKnodeRels(traceIds);
    }

    @PostMapping("/rel/trace/enhancer")
    public List<IdPair> getTraceEnhancerRels(@RequestBody List<Long> traceIds){
        return studyTraceService.getTraceEnhancerRels(traceIds);
    }

    @PutMapping("/rel/trace/knode")
    public void addTraceKnodeRel(@RequestBody IdPair traceKnodeRel){
        studyTraceService.addTraceKnodeRel(traceKnodeRel);
    }

    @PutMapping("/rel/trace/enhancer")
    public void addTraceEnhancerRel(@RequestBody IdPair traceEnhancerRel){
        studyTraceService.addTraceEnhancerRel(traceEnhancerRel);
    }


}
