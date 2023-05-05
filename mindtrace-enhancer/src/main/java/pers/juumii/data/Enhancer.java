package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.handler.MybatisDurationTypeHandler;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.SpringUtils;
import pers.juumii.utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static EnhancerDTO transfer(Enhancer enhancer){
        if(enhancer == null) return null;
        EnhancerDTO res = new EnhancerDTO();
        res.setId(enhancer.getId().toString());
        res.setIntroduction(enhancer.getIntroduction());
        res.setDeleted(enhancer.getDeleted());
        res.setTitle(enhancer.getTitle());
        res.setCreateBy(enhancer.getCreateBy().toString());
        res.setCreateTime(enhancer.getCreateTime().format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
        Opt.ifPresent(enhancer.getLength(), length->res.setLength(length.getSeconds()));
        res.setResourceIds(
            SpringUtils.getBean(ResourceService.class)
            .getResourcesFromEnhancer(enhancer.getId()).stream()
            .map(resource->resource.getId().toString()).toList());
        return res;
    }

    public static List<EnhancerDTO> transfer(List<Enhancer> enhancers){
        return enhancers.stream().map(Enhancer::transfer).toList();
    }
}
