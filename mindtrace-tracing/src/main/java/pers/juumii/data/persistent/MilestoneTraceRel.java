package pers.juumii.data.persistent;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MilestoneTraceRel {
    @TableId
    private Long id;
    private Long milestoneId;
    private Long traceId;
    @TableLogic
    private Boolean deleted;

    public static MilestoneTraceRel prototype(Long milestoneId, Long traceId){
        MilestoneTraceRel res = new MilestoneTraceRel();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setMilestoneId(milestoneId);
        res.setTraceId(traceId);
        res.setDeleted(false);
        return res;
    }

    public static MilestoneTraceRel prototype(String milestoneId, String traceId){
        return prototype(Convert.toLong(milestoneId), Convert.toLong(traceId));
    }
}
