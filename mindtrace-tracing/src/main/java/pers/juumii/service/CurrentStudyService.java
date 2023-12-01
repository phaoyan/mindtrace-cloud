package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.temp.CurrentStudy;

import java.util.List;

@Service
public interface CurrentStudyService {
    CurrentStudy startCurrentStudy();

    CurrentStudy getCurrentStudy();

    void removeCurrentStudy();

    StudyTrace settleCurrentStudy();

    CurrentStudy pauseCurrentStudy();

    CurrentStudy continueCurrentStudy();

    CurrentStudy restartStudy(Long traceId);

    CurrentStudy editCurrentStudyTitle(String title);

    List<Long> addKnodeId(Long knodeId);

    void removeKnodeId(Long knodeId);

    List<Long> addTraceEnhancerRel(Long enhancerId);

    void removeTraceEnhancerRel(Long enhancerId);

    CurrentStudy updateStartTime(String startTime);

    CurrentStudy updateEndTime(String endTime);

}
