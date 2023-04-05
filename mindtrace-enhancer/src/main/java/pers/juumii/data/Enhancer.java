package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.handler.MybatisDurationTypeHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Enhancer {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private String introduction;
    @TableField(exist = false)
    private List<Resource> resources;
    @TableField(typeHandler = MybatisDurationTypeHandler.class)
    private Duration length;
    // Enhancer 的标签用于数据分析
    @TableField(exist = false)
    private List<Label> labels;
    private LocalDateTime createTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createBy;
    private String privacy;
    @TableLogic
    private Boolean deleted;

    public static Enhancer prototype( Long userId) {
        Enhancer res = new Enhancer();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setCreateTime(LocalDateTime.now());
        res.setCreateBy(userId);
        res.setResources(new ArrayList<>());
        res.setLabels(new ArrayList<>());
        return res;
    }
}
