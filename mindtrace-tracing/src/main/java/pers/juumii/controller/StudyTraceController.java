package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.StudyTrace;
import pers.juumii.dto.StudyTraceDTO;
import pers.juumii.service.StudyTraceService;

import java.util.List;

@RestController
public class StudyTraceController {

    private final StudyTraceService studyTraceService;

    @Autowired
    public StudyTraceController(StudyTraceService studyTraceService) {
        this.studyTraceService = studyTraceService;
    }

    @PostMapping("/study/trace")
    public StudyTraceDTO postStudyTrace(@RequestBody StudyTraceDTO data){
        return StudyTrace.transfer(studyTraceService.postStudyTrace(data));
    }

    @GetMapping("/study/trace")
    public List<StudyTraceDTO> getUserStudyTraces(@RequestParam("userId") Long userId){
        return StudyTrace.transfer(studyTraceService.getUserStudyTraces(userId));
    }

    @GetMapping("/study/template/{templateId}/trace")
    public List<StudyTraceDTO> getTemplateStudyTraces(@PathVariable Long templateId){
        return StudyTrace.transfer(studyTraceService.getTemplateStudyTraces(templateId));
    }

    @GetMapping("/study/trace/{traceId}")
    public StudyTraceDTO getStudyTrace(@PathVariable Long traceId){
        return StudyTrace.transfer(studyTraceService.getStudyTrace(traceId));
    }

    @DeleteMapping("/study/trace{traceId}")
    public void removeStudyTrace(@PathVariable Long traceId){
        studyTraceService.removeStudyTrace(traceId);
    }

    @PostMapping("/study/trace/{traceId}/knode/{knodeId}")
    public void postTraceCoverage(@PathVariable Long traceId, @PathVariable Long knodeId){
        studyTraceService.postTraceCoverage(traceId, knodeId);
    }

    @GetMapping("/study/trace/{traceId}/knode")
    public List<String> getTraceCoverages(@PathVariable Long traceId){
        return studyTraceService.getTraceCoverages(traceId).stream().map(Object::toString).toList();
    }

    @GetMapping("/knode/{knodeId}/trace")
    public List<String> getKnodeCoveringTraces(@PathVariable Long knodeId){
        return studyTraceService.getKnodeCoveringTraces(knodeId).stream().map(Object::toString).toList();
    }

    @GetMapping("study/trace/{traceId}/knode/{knodeId}")
    public Boolean checkTraceCoverage(@PathVariable Long traceId, @PathVariable Long knodeId){
        return studyTraceService.checkTraceCoverage(traceId, knodeId);
    }

    @DeleteMapping("study/trace/{traceId}/knode/{knodeId}")
    public void removeTraceCoverage(@PathVariable Long traceId, @PathVariable Long knodeId){
        studyTraceService.removeTraceCoverage(traceId, knodeId);
    }
}
