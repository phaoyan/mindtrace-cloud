package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.handler.MybatisDurationTypeHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Enhancer {

    @TableId
    private Long id;
    private String introduction;
    private List<Resource> resources;
    @TableField(typeHandler = MybatisDurationTypeHandler.class)
    private Duration length;
    // Enhancer 的标签用于数据分析
    private List<Label> labels;
    private LocalDateTime createTime;
    private Long createBy;
    private String privacy;
    @TableLogic
    private Boolean deleted;

    public static Enhancer prototype(EnhancerDTO dto, Long userId){
        Enhancer res = new Enhancer();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setIntroduction(dto.getIntroduction());
        res.setResources(
            dto.getResources()
            .stream().map(Resource::prototype)
            .collect(Collectors.toList()));
        res.setLength(dto.getLength());
        res.setLabels(
            dto.getLabels()
            .stream().map(Label::prototype)
            .collect(Collectors.toList()));
        res.setCreateTime(LocalDateTime.now());
        res.setCreateBy(userId);
        res.setDeleted(false);
        res.setPrivacy("private");
        return res;
    }

}
