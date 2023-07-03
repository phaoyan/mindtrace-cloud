package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.service.StudyTraceQueryService;

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

}
