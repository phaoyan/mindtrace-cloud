package pers.juumii.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnodeDTO {

    private String id;
    private Integer index;
    private String title;
    private List<String> labels;
    private String createBy;
    private String createTime;
    private String stemId;
    private List<String> branchIds;
    private List<String> connectionIds;



}
