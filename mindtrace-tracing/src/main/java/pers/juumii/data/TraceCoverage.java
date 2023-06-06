package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class TraceCoverage {
    @TableId
    private Long id;
    private Long traceId;
    private Long knodeId;
    @TableLogic
    private Boolean deleted;

    public static TraceCoverage prototype(Long traceId, Long knodeId) {
        TraceCoverage res = new TraceCoverage();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTraceId(traceId);
        res.setKnodeId(knodeId);
        return res;
    }
}
