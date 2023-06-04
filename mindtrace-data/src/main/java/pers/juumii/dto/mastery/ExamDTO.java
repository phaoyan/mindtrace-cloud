package pers.juumii.dto.mastery;

import lombok.Data;

@Data
public class ExamDTO {
    private String id;
    private String userId;
    // root knode id
    private String rootId;
    private String examStrategy;
}
