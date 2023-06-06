package pers.juumii.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.config.TimeListTypeHandler;
import pers.juumii.dto.StudyTraceDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudyTrace {

    @TableId
    private Long id;
    private Long userId;
    private Long templateId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> pauseList;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> continueList;
    @TableLogic
    private Boolean deleted;

    public static StudyTrace prototype(StudyTraceDTO data) {
        StudyTrace res = new StudyTrace();
        res.setId(data.getId() == null ? Convert.toLong(data.getId()) : IdUtil.getSnowflakeNextId());
        if(data.getUserId() != null) res.setUserId(Convert.toLong(data.getUserId()));
        if(data.getTemplateId() != null) res.setTemplateId(Convert.toLong(data.getTemplateId()));
        if(data.getStartTime() != null) res.setStartTime(TimeUtils.parse(data.getStartTime()));
        if(data.getEndTime() != null) res.setEndTime(TimeUtils.parse(data.getEndTime()));
        if(data.getPauseList() != null) res.setPauseList(data.getPauseList().stream().map(TimeUtils::parse).toList());
        if(data.getContinueList() != null) res.setContinueList(data.getContinueList().stream().map(TimeUtils::parse).toList());
        res.setDeleted(false);
        return res;
    }

    public static StudyTraceDTO transfer(StudyTrace trace) {
        StudyTraceDTO res = new StudyTraceDTO();
        res.setId(trace.getId().toString());
        res.setUserId(trace.getUserId().toString());
        res.setTemplateId(trace.getTemplateId().toString());
        res.setStartTime(TimeUtils.format(trace.getStartTime()));
        res.setEndTime(TimeUtils.format(trace.getEndTime()));
        return res;
    }

    public static List<StudyTraceDTO> transfer(List<StudyTrace> traces){
        return traces.stream().map(StudyTrace::transfer).toList();
    }
}
