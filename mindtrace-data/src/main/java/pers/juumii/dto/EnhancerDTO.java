package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class EnhancerDTO {

    private String id;
    private String title;
    private String introduction;
    private List<String> resourceIds;
    private Long length;
    private List<String> labels;
    private String createTime;
    private String createBy;
    private Boolean deleted;
}
