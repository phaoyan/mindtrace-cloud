package pers.juumii.dto.tracing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyTimeDistributionDTO {
    private String knodeId;
    private Long seconds;
}
