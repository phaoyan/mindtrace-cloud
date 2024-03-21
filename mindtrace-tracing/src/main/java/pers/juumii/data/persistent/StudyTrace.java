package pers.juumii.data.persistent;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@TableName(autoResultMap = true)
public class StudyTrace {

    @TableId
    private Long id;
    private Long userId;
    private Long milestoneId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long seconds;
    @TableLogic
    private Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyTrace trace = (StudyTrace) o;
        return id.equals(trace.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static StudyTrace prototype(Long userId) {
        StudyTrace res = new StudyTrace();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setUserId(userId == null ? StpUtil.getLoginIdAsLong() : userId);
        res.setStartTime(LocalDateTime.now());
        res.setDeleted(false);
        return res;
    }

    public static StudyTrace transfer(StudyTraceDTO dto){
        if(dto == null) return null;
        StudyTrace res = new StudyTrace();
        res.setId(Convert.toLong(dto.getId()));
        res.setUserId(Convert.toLong(dto.getUserId()));
        res.setMilestoneId(Convert.toLong(dto.getMilestoneId()));
        res.setTitle(dto.getTitle());
        res.setSeconds(dto.getSeconds());
        res.setStartTime(TimeUtils.parse(dto.getStartTime()));
        res.setEndTime(TimeUtils.parse(dto.getEndTime()));
        return res;
    }

    public static StudyTraceDTO transfer(StudyTrace trace) {
        if(trace == null) return null;
        StudyTraceDTO res = new StudyTraceDTO();
        res.setId(Convert.toStr(trace.getId().toString()));
        res.setUserId(Convert.toStr(trace.getUserId().toString()));
        res.setMilestoneId(Convert.toStr(trace.getMilestoneId()));
        res.setTitle(trace.getTitle());
        res.setStartTime(TimeUtils.format(trace.getStartTime()));
        res.setEndTime(TimeUtils.format(trace.getEndTime()));
        res.setSeconds(trace.getSeconds());
        return res;
    }

    public static List<StudyTraceDTO> transfer(List<StudyTrace> traces){
        return traces.stream().map(StudyTrace::transfer).toList();
    }

}
