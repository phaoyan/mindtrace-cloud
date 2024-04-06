package pers.juumii.dto.tracing;

import lombok.Data;
import pers.juumii.dto.enhancer.EnhancerDTO;

import java.util.List;

@Data
public class EnhancerStudyTimelineItem {

    private String start;
    private String end;
    private String enhancerId;
    private EnhancerDTO enhancer;
    private Long duration;
    private Integer periods;
    private List<String> traceIds;


}
