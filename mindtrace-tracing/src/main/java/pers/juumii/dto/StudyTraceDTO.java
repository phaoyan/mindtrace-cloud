package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudyTraceDTO {
    private String id;
    private String userId;
    private String title;
    private String startTime;
    private String endTime;
    private List<String> pauseList;
    private List<String> continueList;
    private Long seconds; // 持续时间秒数
}
