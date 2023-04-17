package pers.juumii.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    private LocalDateTime createTime;
    private Boolean deleted;
    private String stemId;
    private List<String> branchIds;
    private List<String> connectionIds;
    private Boolean isLeaf;

}
