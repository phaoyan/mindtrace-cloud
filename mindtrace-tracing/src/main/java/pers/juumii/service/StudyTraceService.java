package pers.juumii.service;


import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;

import java.util.List;

public interface StudyTraceService {
    StudyTrace addStudyTrace(StudyTraceDTO data);

    StudyTrace insertStudyTrace(StudyTraceDTO data);

    StudyTrace updateStudyTrace(StudyTraceDTO data);

    List<StudyTrace> getUserStudyTraces(Long userId);

    StudyTrace getStudyTrace(Long traceId);

    void removeStudyTrace(Long traceId);

    void addTraceKnodeRel(Long traceId, Long knodeId);

    List<Long> getTraceKnodeRels(Long traceId);

    List<IdPair> getTraceKnodeRels(List<Long> traceIds);

    List<Long> getTraceEnhancerRels(Long traceId);

    List<IdPair> getTraceEnhancerRels(List<Long> traceIds);

    Boolean checkTraceKnodeRel(Long traceId, Long knodeId);

    List<Long> getStudyTracesOfKnode(Long knodeId);

    void removeTraceKnodeRel(Long traceId, Long knodeId);

    List<StudyTrace> getStudyTracesOfKnodeIncludingBeneath(Long knodeId);

    List<StudyTrace> getStudyTracesOfKnodeIncludingBeneathBySlice(Long knodeId, String moment, Integer count);

    List<StudyTrace> getStudyTracesOfKnodeBatch(List<Long> knodeIds);

    List<StudyTrace> getStudyTracesOfEnhancer(Long enhancerId);

    void addTraceEnhancerRel(Long traceId, Long enhancerId);

    List<Long> getTracedEnhancerIdsFromList(List<Long> enhancerIds);

    void removeTraceEnhancerRel(Long traceId, Long enhancerId);
}
