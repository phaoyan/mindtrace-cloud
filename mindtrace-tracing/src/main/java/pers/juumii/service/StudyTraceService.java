package pers.juumii.service;


import pers.juumii.data.StudyTrace;
import pers.juumii.dto.StudyTraceDTO;

import java.util.List;

public interface StudyTraceService {
    StudyTrace postStudyTrace(StudyTraceDTO data);

    List<StudyTrace> getUserStudyTraces(Long userId);

    List<StudyTrace> getTemplateStudyTraces(Long templateId);

    StudyTrace getStudyTrace(Long traceId);

    void removeStudyTrace(Long traceId);

    void postTraceCoverage(Long traceId, Long knodeId);

    List<Long> getTraceCoverages(Long traceId);

    Boolean checkTraceCoverage(Long traceId, Long knodeId);

    List<Long> getKnodeCoveringTraces(Long knodeId);

    void removeTraceCoverage(Long traceId, Long knodeId);
}
