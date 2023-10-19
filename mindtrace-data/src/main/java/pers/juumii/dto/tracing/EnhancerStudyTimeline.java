package pers.juumii.dto.tracing;

import lombok.Data;

import java.util.List;

@Data
public class EnhancerStudyTimeline {

    private String knodeId;
    private Long minInterval;
    private Long minDuration;
    private List<EnhancerStudyTimelineItem> items;


}
