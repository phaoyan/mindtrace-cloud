package pers.juumii.dto.mastery;

import lombok.Data;

import java.util.List;

@Data
public class ExamResultDTO {

    private String id;
    private String rootId;
    private String userId;
    private String startTime;
    private String endTime;
    private List<ExamInteractDTO> interacts;
    private String examStrategy;

}
