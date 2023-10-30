package pers.juumii.dto.tracing;

import lombok.Data;

@Data
public class MilestoneDTO {
    private String id;
    private String knodeId;
    private String userId;
    private String description;
    private String time;
}
