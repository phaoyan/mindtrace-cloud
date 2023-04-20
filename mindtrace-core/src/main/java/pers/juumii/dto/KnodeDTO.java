package pers.juumii.dto;

import lombok.Data;
import pers.juumii.data.Label;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnodeDTO {

    private String id;
    private Integer index;
    private String title;
    private List<Label> labels;
    private String createBy;
    private LocalDateTime createTime;
    private Boolean deleted;
    private String stemId;
    private List<String> branchIds;
    private List<String> connectionIds;
    private Boolean isLeaf;

}
