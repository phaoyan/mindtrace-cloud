package pers.juumii.data.persistent;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.tracing.TraceGroupDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TraceGroup {
    /**
     * TraceGroup用于将多个Trace成组，按组来分析学习轨迹，以解决StudyTrace数据量过大人没法正常通过阅读来得出结论的问题
     * 1. 同一个StudyTrace可以进入不同的TraceGroup，前端展示的时候按照一个默认优先级决定把这个Trace放在哪个组里
     */

    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createTime;
    @TableLogic
    private Boolean deleted;

    public static TraceGroup prototype(){
        TraceGroup res = new TraceGroup();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setDeleted(false);
        res.setCreateTime(LocalDateTime.now());
        res.setTitle("");
        res.setUserId(StpUtil.getLoginIdAsLong());
        return res;
    }

    public static TraceGroupDTO transfer(TraceGroup group){
        TraceGroupDTO res = new TraceGroupDTO();
        res.setId(group.getId().toString());
        res.setUserId(group.getUserId().toString());
        res.setTitle(group.getTitle());
        res.setCreateTime(TimeUtils.format(group.getCreateTime()));
        return res;
    }

    public static List<TraceGroupDTO> transfer(List<TraceGroup> groups){
        return groups.stream().map(TraceGroup::transfer).toList();
    }
}
