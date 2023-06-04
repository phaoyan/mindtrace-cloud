package pers.juumii.dto.mastery;

import lombok.Data;

import java.util.List;

@Data
public class ExamSessionDTO {

    private String id;
    private ExamDTO exam;
    private String startTime;
    private String endTime;
    private List<ExamInteractDTO> interacts;
}
