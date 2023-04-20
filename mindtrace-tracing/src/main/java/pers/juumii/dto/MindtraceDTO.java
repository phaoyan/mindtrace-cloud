package pers.juumii.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.data.Mindtrace;
import pers.juumii.utils.TimeUtils;

import java.util.List;

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

    public static MindtraceDTO transfer(Mindtrace mindtrace){
        MindtraceDTO res = new MindtraceDTO();
        res.setId(mindtrace.getId().toString());
        res.setEnhancerId(mindtrace.getEnhancerId().toString());
        res.setKnodeId(mindtrace.getKnodeId().toString());
        res.setCreateBy(mindtrace.getCreateBy().toString());
        res.setRetentionAfter(mindtrace.getRetentionAfter());
        res.setRetentionBefore(mindtrace.getRetentionBefore());
        res.setReviewLayer(mindtrace.getReviewLayer());
        res.setRemark(mindtrace.getRemark());
        res.setCreateTime(mindtrace.getCreateTime().format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
        return res;
    }

    public static List<MindtraceDTO> transfer(List<Mindtrace> knodeTrace) {
        return knodeTrace.stream().map(MindtraceDTO::transfer).toList();
    }
}
