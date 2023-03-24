package pers.juumii.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabelDTO {

    private String name;
    private Boolean deleted;
    private LocalDateTime createTime;
    private Long createBy;
}
