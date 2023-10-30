package pers.juumii.data.persistent;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MilestoneResourceRel {
    @TableId
    private Long id;
    private Long milestoneId;
    private Long resourceId;
    @TableLogic
    private Boolean deleted;


    public static MilestoneResourceRel prototype(Long milestoneId, Long resourceId){
        MilestoneResourceRel res = new MilestoneResourceRel();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setMilestoneId(milestoneId);
        res.setResourceId(resourceId);
        res.setDeleted(false);
        return res;
    }
}

