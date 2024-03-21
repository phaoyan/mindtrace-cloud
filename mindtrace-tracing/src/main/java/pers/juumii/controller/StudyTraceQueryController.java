package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.dto.tracing.EnhancerStudyTimeline;
import pers.juumii.dto.tracing.StudyTraceEnhancerInfo;
import pers.juumii.dto.tracing.StudyTraceKnodeInfo;
import pers.juumii.service.StudyTraceQueryService;
import pers.juumii.service.StudyTraceService;

import java.util.List;
import java.util.Map;

@RestController
public class StudyTraceQueryController {

    private final StudyTraceQueryService studyTraceQueryService;

    @Autowired
    public StudyTraceQueryController(StudyTraceQueryService studyTraceQueryService) {
        this.studyTraceQueryService = studyTraceQueryService;
    }

    @GetMapping("/study/knode/{knodeId}")
    public List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(@PathVariable Long knodeId){
        return studyTraceQueryService.getStudyTraceKnodeInfo(knodeId);
    }

    @GetMapping("/study/enhancer/{enhancerId}")
    public StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(@PathVariable Long enhancerId){
        return studyTraceQueryService.getStudyTraceEnhancerInfo(enhancerId);
    }

    @GetMapping("/study/knode/{knodeId}/enhancer")
    public List<StudyTraceEnhancerInfo> getStudyTraceEnhancerInfoUnderKnode(@PathVariable Long knodeId){
        return studyTraceQueryService.getStudyTraceEnhancerInfoUnderKnode(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/timeline/enhancer")
    public EnhancerStudyTimeline getEnhancerStudyTimeline(
            @PathVariable Long knodeId,
            @RequestParam Long minDuration,
            @RequestParam Long minInterval){
        return studyTraceQueryService.getEnhancerStudyTimeline(knodeId, minDuration, minInterval);
    }

    @GetMapping("/study/knode/{knodeId}/info/current-month-study-time")
    public Long currentMonthStudyTime(@PathVariable Long knodeId){
        return studyTraceQueryService.currentMonthStudyTime(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/info/study-time-accumulation")
    public Map<String, Long> studyTimeAccumulation(@PathVariable Long knodeId){
        return studyTraceQueryService.studyTimeAccumulation(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/info/trace-count")
    public Integer traceCount(@PathVariable Long knodeId){
        return studyTraceQueryService.traceCount(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/info/calendar-day")
    public Map<String, Long> calendarDay(@PathVariable Long knodeId){
        return studyTraceQueryService.calendarDay(knodeId);
    }

    @GetMapping("/study/knode/{knodeId}/info/calendar-month")
    public Map<String, Long> calendarMonth(@PathVariable Long knodeId){
        return studyTraceQueryService.calendarMonth(knodeId);
    }

}
