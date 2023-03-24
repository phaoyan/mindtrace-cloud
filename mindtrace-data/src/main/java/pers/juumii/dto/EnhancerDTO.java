package pers.juumii.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnhancerDTO {

    private Long id;
    private String introduction;
    private List<ResourceDTO> resources;
    private Duration length;
    // Enhancer 的标签用于数据分析
    private List<LabelDTO> labels;
    private LocalDateTime createTime;
    private Long createBy;
    private String privacy;
    private Boolean deleted;
}
