package pers.juumii.dto.tracing;

import lombok.Data;

import java.util.List;

@Data
public class CurrentStudyDTO {
    private StudyTraceDTO trace;
    private List<String> knodeIds;
    private List<String> enhancerIds;
    private List<String> continueList;
    private List<String> pauseList;
}
