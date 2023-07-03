package pers.juumii.service;

import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceCoverage;
import pers.juumii.data.temp.CurrentStudy;
import pers.juumii.dto.CurrentStudyDTO;

import java.util.List;

public interface CurrentStudyService {
    CurrentStudy startCurrentStudy(Long userId);

    CurrentStudy getCurrentStudy();

    void removeCurrentStudy();

    StudyTrace settleCurrentStudy();

    CurrentStudy pauseCurrentStudy();

    CurrentStudy continueCurrentStudy();

    CurrentStudy editCurrentStudyTitle(String title);

    List<TraceCoverage> addTraceCoverage(List<Long> knodeIds);

    void removeTraceCoverage(Long knodeId);

}
