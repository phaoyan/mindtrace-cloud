package pers.juumii.data.persistent;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.tracing.MilestoneDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Milestone {
    @TableId
    private Long id;
    private Long knodeId;
    private Long userId;
    private String description;
    private LocalDateTime time;
    @TableLogic
    private Boolean deleted;


    public static Milestone prototype(Long knodeId, Long userId){
        Milestone res = new Milestone();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTime(LocalDateTime.now());
        res.setKnodeId(knodeId);
        res.setUserId(userId);
        res.setDescription("");
        res.setDeleted(false);
        return res;
    }

    public static MilestoneDTO transfer(Milestone milestone){
        MilestoneDTO res = new MilestoneDTO();
        res.setId(milestone.getId().toString());
        res.setTime(TimeUtils.format(milestone.getTime()));
        res.setKnodeId(milestone.getKnodeId().toString());
        res.setUserId(milestone.getUserId().toString());
        res.setDescription(milestone.getDescription());
        return res;
    }

    public static List<MilestoneDTO> transfer(List<Milestone> milestones){
        return milestones.stream().map(Milestone::transfer).toList();
    }
}
