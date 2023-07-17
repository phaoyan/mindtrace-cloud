package pers.juumii.service;

import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.temp.CurrentStudy;

import java.util.List;

public interface CurrentStudyService {
    CurrentStudy startCurrentStudy(Long userId);

    CurrentStudy getCurrentStudy();

    void removeCurrentStudy();

    StudyTrace settleCurrentStudy();

    CurrentStudy pauseCurrentStudy();

    CurrentStudy continueCurrentStudy();

    CurrentStudy editCurrentStudyTitle(String title);

    List<Long> addKnodeId(Long knodeId);

    void removeKnodeId(Long knodeId);

    List<Long> addTraceEnhancerRel(Long enhancerId);

    void removeTraceEnhancerRel(Long enhancerId);

}
