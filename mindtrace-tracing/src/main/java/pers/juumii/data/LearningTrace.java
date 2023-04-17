package pers.juumii.data;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import pers.juumii.config.TimeListTypeHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 为了避免客户端意外关闭导致学习记录中断的情况，学习记录在后端用LearningTrace同步暂存
 * Learning Trace 对应唯一确定的 Enhancer，但不对应唯一Knode
 * 提供函数，结合每个Knode的学习信息生成若干Mindtrace存入数据库
 * Learning Trace 与相关联的 Mindtrace 之间存在引用，数据分析可能用到
 */
@Data
@TableName(autoResultMap = true)
public class LearningTrace {

    @TableId
    private Long id;
    private Long enhancerId;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> pauseList;
    @TableField(typeHandler = TimeListTypeHandler.class)
    private List<LocalDateTime> continueList;
    @TableLogic
    private Boolean deleted;


    public static LearningTrace prototype(Long userId, Long enhancerId) {
        LearningTrace res = new LearningTrace();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setCreateBy(userId);
        res.setEnhancerId(enhancerId);
        res.setCreateTime(LocalDateTime.now());
        res.setPauseList(new ArrayList<>());
        res.setContinueList(new ArrayList<>());
        res.setDeleted(false);
        return res;
    }
}
