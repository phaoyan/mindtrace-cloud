package pers.juumii.dto.enhancer;

import lombok.Data;

import java.util.List;

@Data
public class EnhancerDTO {

    private String id;
    private String title;
    private Boolean isQuiz;
    private Long length;
    private String createTime;
    private String createBy;
    private Boolean deleted;
}
