package pers.juumii.data.persistent;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.juumii.config.TimeListTypeHandler;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@TableName(autoResultMap = true)
public class StudyTrace {

    @TableId
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> pauseList;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> continueList;
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

    public Duration duration() {
        if(getStartTime() == null ||
            getEndTime() == null ||
            getPauseList() == null ||
            getContinueList() == null)
            return Duration.ZERO;
        Duration duration = Duration.between(getStartTime(), getEndTime());
        for(int i = 0; i < Math.min(getPauseList().size(), getContinueList().size()); i ++)
            duration = duration.minus(Duration.between(getPauseList().get(i), getContinueList().get(i)));
        if(getPauseList().size() > getContinueList().size())
            duration = duration.minus(Duration.between(getPauseList().get(getPauseList().size() - 1), getEndTime()));
        return duration;
    }

    public static StudyTrace prototype(Long userId) {
        StudyTrace res = new StudyTrace();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setUserId(userId == null ? StpUtil.getLoginIdAsLong() : userId);
        res.setStartTime(LocalDateTime.now());
        res.setContinueList(new ArrayList<>());
        res.setPauseList(new ArrayList<>());
        res.setDeleted(false);
        return res;
    }

    public static StudyTrace transfer(StudyTraceDTO dto){
        if(dto == null) return null;
        StudyTrace res = new StudyTrace();
        res.setId(Convert.toLong(dto.getId()));
        res.setUserId(Convert.toLong(dto.getUserId()));
        res.setTitle(dto.getTitle());
        res.setPauseList(dto.getPauseList() != null ? new ArrayList<>(dto.getPauseList().stream().map(TimeUtils::parse).toList()) : new ArrayList<>());
        res.setContinueList(dto.getContinueList() != null ? new ArrayList<>(dto.getContinueList().stream().map(TimeUtils::parse).toList()): new ArrayList<>());
        res.setStartTime(TimeUtils.parse(dto.getStartTime()));
        res.setEndTime(TimeUtils.parse(dto.getEndTime()));
        return res;
    }

    public static StudyTraceDTO transfer(StudyTrace trace) {
        if(trace == null) return null;
        StudyTraceDTO res = new StudyTraceDTO();
        res.setId(trace.getId().toString());
        res.setUserId(trace.getUserId().toString());
        res.setTitle(trace.getTitle());
        res.setStartTime(TimeUtils.format(trace.getStartTime()));
        res.setEndTime(TimeUtils.format(trace.getEndTime()));
        if(trace.getContinueList() != null)
            res.setContinueList(trace.getContinueList().stream().map(TimeUtils::format).toList());
        if(trace.getPauseList() != null)
            res.setPauseList(trace.getPauseList().stream().map(TimeUtils::format).toList());
        res.setSeconds(trace.duration().getSeconds());
        return res;
    }

    public static List<StudyTraceDTO> transfer(List<StudyTrace> traces){
        return traces.stream().map(StudyTrace::transfer).toList();
    }

}
