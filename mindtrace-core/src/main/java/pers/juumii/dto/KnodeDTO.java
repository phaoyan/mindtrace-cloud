package pers.juumii.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.data.Label;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnodeDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private List<Label> labels;
    private LocalDateTime createTime;
    private Boolean deleted;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long stemId;
    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> branchIds;
    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> connectionIds;

}
