package pers.juumii.dto.enhancer;

import lombok.Data;

@Data
public class ResourceDTO {
    private String id;
    private String title;
    private String type;
    private String createTime;
    private String createBy;
}
