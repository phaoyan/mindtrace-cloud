package pers.juumii.service;


import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.data.persistent.TraceKnodeRel;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;

import java.util.List;

public interface StudyTraceService {
    StudyTrace addStudyTrace(StudyTraceDTO data);

    StudyTrace insertStudyTrace(StudyTraceDTO data);

    StudyTrace updateStudyTrace(StudyTraceDTO data);

    List<StudyTrace> getUserStudyTraces(Long userId);

    List<StudyTrace> getAllStudyTraces();

    StudyTrace getStudyTrace(Long traceId);

    void removeStudyTrace(Long traceId);

    void postTraceCoverage(Long traceId, Long knodeId);

    List<TraceKnodeRel> getAllTraceKnodeRels();

    List<Long> getTraceKnodeRels(Long traceId);

    List<IdPair> getTraceKnodeRels(List<Long> traceIds);

    List<TraceEnhancerRel> getAllTraceEnhancerRels();

    List<Long> getTraceEnhancerRels(Long traceId);

    List<IdPair> getTraceEnhancerRels(List<Long> traceIds);

    Boolean checkTraceKnodeRel(Long traceId, Long knodeId);

    List<Long> getKnodeCoveringTraces(Long knodeId);

    void removeTraceCoverage(Long traceId, Long knodeId);

    List<StudyTrace> getStudyTracesOfKnode(Long knodeId);

    List<StudyTrace> getStudyTracesOfKnodeBatch(List<Long> knodeIds);

    List<StudyTrace> getStudyTracesOfEnhancer(Long enhancerId);


    void addTraceKnodeRel(IdPair traceKnodeRel);

    void addTraceEnhancerRel(IdPair traceEnhancerRel);

    void removeTraceEnhancerRel(Long id);

    void removeTraceKnodeRel(Long id);
}
