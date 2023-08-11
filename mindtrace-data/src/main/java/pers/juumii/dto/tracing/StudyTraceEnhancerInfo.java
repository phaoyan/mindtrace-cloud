package pers.juumii.dto.tracing;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StudyTraceEnhancerInfo {
    private String enhancerId;
    private Long duration; //学习时长
    private Integer review; //学习次数
    private List<String> moments; //各次学习的起始时间的字符串
    private Map<String, Long> momentsWithDuration;
}
