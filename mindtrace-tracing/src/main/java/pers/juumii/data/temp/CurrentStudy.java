package pers.juumii.data.temp;

import lombok.Data;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceCoverage;
import pers.juumii.dto.CurrentStudyDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class CurrentStudy {

    private StudyTrace trace;
    private List<TraceCoverage> coverages;

    public static CurrentStudy prototype(StudyTrace trace) {
        CurrentStudy res = new CurrentStudy();
        res.setTrace(trace);
        res.setCoverages(new ArrayList<>());
        return res;
    }

    public static CurrentStudyDTO transfer(CurrentStudy current) {
        if(current == null) return null;
        CurrentStudyDTO res = new CurrentStudyDTO();
        res.setTrace(StudyTrace.transfer(current.getTrace()));
        res.setCoverages(TraceCoverage.transfer(current.getCoverages()));
        return res;
    }

    public static CurrentStudy transfer(CurrentStudyDTO dto){
        if(dto == null) return null;
        CurrentStudy res = new CurrentStudy();
        res.setTrace(StudyTrace.transfer(dto.getTrace()));
        res.setCoverages(TraceCoverage.transfer(dto.getCoverages(), true));
        return res;
    }
}
