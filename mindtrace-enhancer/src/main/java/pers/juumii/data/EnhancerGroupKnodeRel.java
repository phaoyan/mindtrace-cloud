package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerGroupKnodeRel {
    private Long knodeId;
    private Long groupId;
    private Integer groupIndex;
    @TableLogic
    private Boolean deleted;

    public static EnhancerGroupKnodeRel prototype(Long groupId, Long knodeId, Integer groupIndex){
        EnhancerGroupKnodeRel res = new EnhancerGroupKnodeRel();
        res.setKnodeId(knodeId);
        res.setGroupId(groupId);
        res.setGroupIndex(groupIndex);
        res.setDeleted(false);
        return res;
    }
}
