package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudyTraceDTO {
    private String id;
    private String userId;
    private String templateId;
    private String startTime;
    private String endTime;
    private List<String> pauseList;
    private List<String> continueList;
}
