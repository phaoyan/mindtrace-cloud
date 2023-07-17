package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.dto.StudyTraceEnhancerInfo;
import pers.juumii.dto.StudyTraceKnodeInfo;
import pers.juumii.service.StudyTraceQueryService;

import java.util.List;
import java.util.Map;

@RestController
public class StudyTraceQueryController {

    private final StudyTraceQueryService studyTraceQueryService;

    @Autowired
    public StudyTraceQueryController(StudyTraceQueryService studyTraceQueryService) {
        this.studyTraceQueryService = studyTraceQueryService;
    }

    @GetMapping("/study/knode/{knodeId}/time/distribution")
    public Map<String, Long> getStudyTimeDistribution(@PathVariable Long knodeId){
        return studyTraceQueryService.getStudyTimeDistribution(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}")
    public List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(@PathVariable Long knodeId){
        return studyTraceQueryService.getStudyTraceKnodeInfo(knodeId);
    }

    @GetMapping("/study/enhancer/{enhancerId}")
    public StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(@PathVariable Long enhancerId){
        return studyTraceQueryService.getStudyTraceEnhancerInfo(enhancerId);
    }

}
