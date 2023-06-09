package pers.juumii.service;


import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.StudyTraceDTO;

import java.util.List;

public interface StudyTraceService {
    StudyTrace postStudyTrace(StudyTraceDTO data);

    StudyTrace insertStudyTrace(StudyTraceDTO data);

    StudyTrace updateStudyTrace(StudyTraceDTO data);

    List<StudyTrace> getUserStudyTraces(Long userId);

    StudyTrace getStudyTrace(Long traceId);

    void removeStudyTrace(Long traceId);

    void postTraceCoverage(Long traceId, Long knodeId);

    List<Long> getTraceCoverages(Long traceId);

    Boolean checkTraceCoverage(Long traceId, Long knodeId);

    List<Long> getKnodeCoveringTraces(Long knodeId);

    void removeTraceCoverage(Long traceId, Long knodeId);

    List<StudyTrace> getStudyTracesOfKnode(Long knodeId);

}
