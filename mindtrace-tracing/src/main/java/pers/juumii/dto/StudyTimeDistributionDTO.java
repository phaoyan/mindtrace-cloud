package pers.juumii.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyTimeDistributionDTO {
    private String knodeId;
    private Long seconds;
}
