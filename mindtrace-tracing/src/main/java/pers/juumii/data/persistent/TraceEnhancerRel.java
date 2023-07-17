package pers.juumii.data.persistent;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.TraceEnhancerRelDTO;

import java.util.List;

@Data
public class TraceEnhancerRel {
    @TableId
    private Long id;
    private Long traceId;
    private Long enhancerId;
    @TableLogic
    private Long deleted;

    public static TraceEnhancerRel prototype(Long traceId, Long enhancerId){
        TraceEnhancerRel res = new TraceEnhancerRel();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTraceId(traceId);
        res.setEnhancerId(enhancerId);
        return res;
    }

    public static List<TraceEnhancerRelDTO> transfer(List<TraceEnhancerRel> enhancers) {
        return enhancers.stream().map(TraceEnhancerRel::transfer).toList();
    }

    public static TraceEnhancerRelDTO transfer(TraceEnhancerRel enhancer) {
        TraceEnhancerRelDTO res = new TraceEnhancerRelDTO();
        res.setId(enhancer.getEnhancerId().toString());
        res.setTraceId(enhancer.getTraceId().toString());
        res.setEnhancerId(enhancer.getEnhancerId().toString());
        return res;
    }
}
