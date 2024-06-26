package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.SpringUtils;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Enhancer {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private Boolean isQuiz;
    private LocalDateTime createTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createBy;
    @TableLogic
    private Boolean deleted;

    public static Enhancer prototype(Long userId) {
        Enhancer res = new Enhancer();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setCreateTime(LocalDateTime.now());
        res.setCreateBy(userId);
        res.setIsQuiz(true);
        return res;
    }

    public static EnhancerDTO transfer(Enhancer enhancer){
        if(enhancer == null) return null;
        EnhancerDTO res = new EnhancerDTO();
        res.setId(enhancer.getId().toString());
        res.setDeleted(enhancer.getDeleted());
        res.setTitle(enhancer.getTitle());
        res.setIsQuiz(enhancer.getIsQuiz());
        res.setCreateBy(enhancer.getCreateBy().toString());
        res.setCreateTime(enhancer.getCreateTime().format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
        return res;
    }

    public static List<EnhancerDTO> transfer(List<Enhancer> enhancers){
        return enhancers.stream().map(Enhancer::transfer).toList();
    }
}
