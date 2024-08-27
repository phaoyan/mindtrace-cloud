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

    List<Long> getKnodeIdsByTraceId(Long traceId);

    List<Long> getEnhancerIdsByTraceId(Long traceId);

    List<IdPair> getEnhancerIdsByTraceId(List<Long> traceIds);

    List<StudyTrace> getStudyTracesOfKnodeIncludingBeneath(Long knodeId);

    List<StudyTrace> getStudyTracesOfKnodeBatch(List<Long> knodeIds);

    List<StudyTrace> getStudyTracesOfEnhancer(Long enhancerId);

    void addTraceEnhancerRel(Long traceId, Long enhancerId);

    List<Long> getTracedEnhancerIdsFromList(List<Long> enhancerIds);

    void removeTraceEnhancerRel(Long traceId, Long enhancerId);
}
