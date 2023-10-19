package pers.juumii.service;

import pers.juumii.dto.tracing.EnhancerStudyTimeline;
import pers.juumii.dto.tracing.EnhancerStudyTimelineItem;
import pers.juumii.dto.tracing.StudyTraceEnhancerInfo;
import pers.juumii.dto.tracing.StudyTraceKnodeInfo;

import java.util.List;

public interface StudyTraceQueryService {

    StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(Long enhancerId);

    List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId);

    List<StudyTraceEnhancerInfo> getStudyTraceEnhancerInfoUnderKnode(Long knodeId);

    EnhancerStudyTimeline getEnhancerStudyTimeline(Long knodeId, Long minDuration, Long minInterval);

}
