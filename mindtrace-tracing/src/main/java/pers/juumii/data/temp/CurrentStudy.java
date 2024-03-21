package pers.juumii.data.temp;

import cn.hutool.core.convert.Convert;
import lombok.Data;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.tracing.CurrentStudyDTO;
import pers.juumii.utils.TimeUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class CurrentStudy {

    private StudyTrace trace;
    private List<Long> knodeIds;
    private List<Long> enhancerIds;
    private List<String> continueList;
    private List<String> pauseList;

    public Long duration() {
        Duration duration = Duration.between(trace.getStartTime(), trace.getEndTime());
        for(int i = 0; i < Math.min(getPauseList().size(), getContinueList().size()); i ++)
            duration = duration.minus(Duration.between(
                    TimeUtils.parse(getPauseList().get(i)),
                    TimeUtils.parse(getContinueList().get(i))));
        if(getPauseList().size() > getContinueList().size())
            duration = duration.minus(Duration.between(
                    TimeUtils.parse(getPauseList().get(getPauseList().size() - 1)),
                    trace.getEndTime()));
        return duration.toSeconds();
    }

    public static CurrentStudy prototype(StudyTrace trace) {
        CurrentStudy res = new CurrentStudy();
        res.setTrace(trace);
        res.setKnodeIds(new ArrayList<>());
        res.setEnhancerIds(new ArrayList<>());
        res.setContinueList(new ArrayList<>());
        res.setPauseList(new ArrayList<>());
        return res;
    }

    public static CurrentStudyDTO transfer(CurrentStudy current) {
        if(current == null) return null;
        CurrentStudyDTO res = new CurrentStudyDTO();
        res.setTrace(StudyTrace.transfer(current.getTrace()));
        res.setKnodeIds(new ArrayList<>(current.getKnodeIds().stream().map(Object::toString).toList()));
        res.setEnhancerIds(new ArrayList<>(current.getEnhancerIds().stream().map(Object::toString).toList()));
        res.setContinueList(current.getContinueList());
        res.setPauseList(current.getPauseList());
        return res;
    }

    public static CurrentStudy transfer(CurrentStudyDTO dto){
        if(dto == null) return null;
        CurrentStudy res = new CurrentStudy();
        res.setTrace(StudyTrace.transfer(dto.getTrace()));
        res.setKnodeIds(new ArrayList<>(dto.getKnodeIds().stream().map(Convert::toLong).toList()));
        res.setEnhancerIds(new ArrayList<>(dto.getEnhancerIds().stream().map(Convert::toLong).toList()));
        res.setContinueList(dto.getContinueList());
        res.setPauseList(dto.getPauseList());
        return res;
    }


}
