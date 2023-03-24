package pers.juumii.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnodeDTO {

    private Long id;
    private String title;
    private List<LabelDTO> labels;
    private LocalDateTime createTime;
    private Boolean deleted;
    private Long stemId;
    private List<Long> branchIds;
    private List<Long> connectionIds;

}
