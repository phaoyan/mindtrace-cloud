package pers.juumii.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceDTO {

    private Long id;
    private String url;
    // Resource的type用于资源处理
    private String type;
    private LocalDateTime createTime;
    private Long createBy;
    private String privacy;
}
