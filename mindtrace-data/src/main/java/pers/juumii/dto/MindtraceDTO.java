package pers.juumii.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class MindtraceDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @JsonSerialize(using = ToStringSerializer.class)
    private String enhancerId;
    @JsonSerialize(using = ToStringSerializer.class)
    private String knodeId;
    @JsonSerialize(using = ToStringSerializer.class)
    private String createBy;
    private Double retentionAfter;
    private Double retentionBefore;
    private Integer reviewLayer = 1;
    private String createTime;
    private String remark;
}
