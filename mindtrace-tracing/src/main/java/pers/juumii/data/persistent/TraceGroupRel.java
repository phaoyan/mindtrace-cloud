package pers.juumii.data.persistent;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class TraceGroupRel {
    private Long id;
    private Long traceId;
    private Long groupId;
    @TableLogic
    private Boolean deleted;


    public static TraceGroupRel prototype(Long traceId, Long groupId){
        TraceGroupRel res = new TraceGroupRel();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTraceId(traceId);
        res.setGroupId(groupId);
        return res;
    }
}
