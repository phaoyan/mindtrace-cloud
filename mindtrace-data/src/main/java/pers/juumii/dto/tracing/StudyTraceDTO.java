package pers.juumii.dto.tracing;

import lombok.Data;

import java.util.List;

@Data
public class StudyTraceDTO {
    private String id;
    private String userId;
    private String milestoneId;
    private String title;
    private String startTime;
    private String endTime;
    private Long seconds; // 持续时间秒数
}
