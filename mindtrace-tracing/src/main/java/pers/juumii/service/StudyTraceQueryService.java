package pers.juumii.service;

import pers.juumii.dto.StudyTraceEnhancerInfo;
import pers.juumii.dto.StudyTraceKnodeInfo;

import java.util.List;
import java.util.Map;

public interface StudyTraceQueryService {

    StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(Long enhancerId);

    List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId);

    List<StudyTraceEnhancerInfo> getStudyTraceEnhancerInfoUnderKnode(Long knodeId);

}
