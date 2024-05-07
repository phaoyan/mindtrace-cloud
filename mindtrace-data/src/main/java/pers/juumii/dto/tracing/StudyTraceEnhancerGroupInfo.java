package pers.juumii.dto.tracing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyTraceEnhancerGroupInfo {
    private String groupId;
    private String title;
    private Long duration; //学习时长
    private Integer review; //学习次数
    private List<String> traceIds;
}
