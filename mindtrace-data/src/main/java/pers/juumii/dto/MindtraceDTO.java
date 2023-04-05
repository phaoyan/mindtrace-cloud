package pers.juumii.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class MindtraceDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long enhancerId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knodeId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createBy;
    private Double retentionAfter;
    private Double retentionBefore;
    private Integer reviewLayer = 1;
    private LocalDateTime createTime;
    private Duration duration;
    private String remark;
}
