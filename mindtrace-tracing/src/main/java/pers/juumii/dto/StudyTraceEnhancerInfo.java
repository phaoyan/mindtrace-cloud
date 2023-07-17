package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudyTraceEnhancerInfo {
    private String enhancerId;
    private Long duration; //学习时长
    private Integer review; //学习次数
    private List<String> moments; //各次学习的起始时间的字符串
}
