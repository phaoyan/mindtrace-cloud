package pers.juumii.service;

import pers.juumii.dto.tracing.EnhancerStudyTimeline;
import pers.juumii.dto.tracing.StudyTraceEnhancerGroupInfo;
import pers.juumii.dto.tracing.StudyTraceEnhancerInfo;
import pers.juumii.dto.tracing.StudyTraceKnodeInfo;

import java.util.List;
import java.util.Map;

public interface StudyTraceQueryService {

    StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(Long enhancerId);

    StudyTraceEnhancerGroupInfo getStudyTraceEnhancerGroupInfo(Long groupId);

    List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId);

    List<StudyTraceEnhancerInfo> getStudyTraceEnhancerInfoUnderKnode(Long knodeId);

    EnhancerStudyTimeline getEnhancerStudyTimeline(Long knodeId, Long minDuration, Long minInterval);

    Long currentMonthStudyTime(Long knodeId);

    Map<String, Long> studyTimeAccumulation(Long knodeId);

    Integer traceCount(Long knodeId);

    Map<String, Long> calendarDay(Long knodeId);

    Map<String, Long> calendarMonth(Long knodeId);

}
