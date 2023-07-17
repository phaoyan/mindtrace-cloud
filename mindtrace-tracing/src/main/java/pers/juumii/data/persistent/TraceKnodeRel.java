package pers.juumii.data.persistent;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.TraceCoverageDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class TraceKnodeRel {
    @TableId
    private Long id;
    private Long traceId;
    private Long knodeId;
    @TableLogic
    private Boolean deleted;

    public static TraceKnodeRel prototype(Long traceId, Long knodeId) {
        TraceKnodeRel res = new TraceKnodeRel();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTraceId(traceId);
        res.setKnodeId(knodeId);
        return res;
    }

    public static TraceKnodeRel transfer(TraceCoverageDTO dto){
        TraceKnodeRel res = new TraceKnodeRel();
        res.setId(Convert.toLong(dto.getId()));
        res.setKnodeId(Convert.toLong(dto.getKnodeId()));
        res.setTraceId(Convert.toLong(dto.getTraceId()));
        return res;
    }

    public static List<TraceKnodeRel> transfer(List<TraceCoverageDTO> dto, Boolean flag){
        if(dto == null) return null;
        return new ArrayList<>(dto.stream().map(TraceKnodeRel::transfer).toList());
    }

    public static TraceCoverageDTO transfer(TraceKnodeRel coverage){
        TraceCoverageDTO res = new TraceCoverageDTO();
        res.setId(coverage.getId().toString());
        res.setTraceId(coverage.getTraceId().toString());
        res.setKnodeId(coverage.getKnodeId().toString());
        return res;
    }

    public static List<TraceCoverageDTO> transfer(List<TraceKnodeRel> coverages) {
        if(coverages == null) return null;
        return new ArrayList<>(coverages.stream().map(TraceKnodeRel::transfer).toList());
    }
}
