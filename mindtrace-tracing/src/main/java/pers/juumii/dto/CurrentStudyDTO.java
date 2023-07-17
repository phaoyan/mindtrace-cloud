package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class CurrentStudyDTO {
    private StudyTraceDTO trace;
    private List<String> knodeIds;
    private List<String> enhancerIds;

}
