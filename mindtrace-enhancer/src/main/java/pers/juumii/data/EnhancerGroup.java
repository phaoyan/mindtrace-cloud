package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.enhancer.EnhancerGroupDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnhancerGroup {

    @TableId
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createTime;
    @TableLogic
    private Boolean deleted;

    public static EnhancerGroup prototype(Long userId){
        EnhancerGroup res = new EnhancerGroup();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTitle("");
        res.setCreateTime(LocalDateTime.now());
        res.setUserId(userId);
        res.setDeleted(false);
        return res;
    }

    public static EnhancerGroupDTO transfer(EnhancerGroup group){
        EnhancerGroupDTO res = new EnhancerGroupDTO();
        res.setId(group.getId().toString());
        res.setTitle(group.getTitle());
        res.setUserId(group.getUserId());
        res.setCreateTime(TimeUtils.format(group.getCreateTime()));
        return res;
    }

    public static List<EnhancerGroupDTO> transfer(List<EnhancerGroup> enhancerGroups){
        return enhancerGroups.stream().map(EnhancerGroup::transfer).toList();
    }

}
