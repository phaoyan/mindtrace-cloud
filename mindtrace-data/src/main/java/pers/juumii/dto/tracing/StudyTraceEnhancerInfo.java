package pers.juumii.dto.tracing;

import lombok.Data;

import java.util.List;

@Data
public class StudyTraceEnhancerInfo {
    private String enhancerId;
    private Long duration; //学习时长
    private Integer review; //学习次数
    private List<StudyTraceDTO> traces;
}
