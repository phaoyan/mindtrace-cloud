package pers.juumii.dto;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import lombok.Data;
import pers.juumii.data.LearningTrace;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.TimeUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class LearningTraceDTO {
    private String id;
    private String enhancerId;
    private String createBy;
    private String createTime;
    private String finishTime;
    private List<String> pauseList;
    private List<String> continueList;
    private List<Long> relatedKnodeIds;

    public static LearningTraceDTO transfer(LearningTrace ori){
        LearningTraceDTO res = new LearningTraceDTO();
        res.setId(ori.getId().toString());
        res.setCreateBy(ori.getCreateBy().toString());
        res.setEnhancerId(ori.getEnhancerId().toString());
        Opt.ifPresent(ori.getCreateTime(), (time)->
            res.setCreateTime(LocalDateTimeUtil.format(time, DateTimeFormatter.ofPattern(TimeUtils.DEFAULT_DATE_TIME_PATTERN))));
        Opt.ifPresent(ori.getFinishTime(), (time)->
            res.setFinishTime(LocalDateTimeUtil.format(time, DateTimeFormatter.ofPattern(TimeUtils.DEFAULT_DATE_TIME_PATTERN))));
        Opt.ifPresent(ori.getPauseList(), list->
            res.setPauseList(DataUtils.destructureAll(list, time->time.format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER))));
        Opt.ifPresent(ori.getContinueList(), list->
            res.setContinueList(DataUtils.destructureAll(list, time->time.format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER))));
        return res;
    }

    public static LearningTraceDTO transfer(LearningTrace ori, List<Long> relatedKnodeIds){
        LearningTraceDTO transfer = transfer(ori);
        transfer.setRelatedKnodeIds(relatedKnodeIds);
        return transfer;
    }

    public static List<LearningTraceDTO> transfer(List<LearningTrace> ori){
        return ori.stream().map(LearningTraceDTO::transfer).toList();
    }
}
