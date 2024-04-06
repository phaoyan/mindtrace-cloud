package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerGroupRel {

    private Long enhancerId;
    private Long groupId;
    private Integer enhancerIndex;
    @TableLogic
    private Boolean deleted;

    public static EnhancerGroupRel prototype(Long enhancerId, Long groupId, Integer enhancerIndex){
        EnhancerGroupRel res = new EnhancerGroupRel();
        res.setGroupId(groupId);
        res.setEnhancerId(enhancerId);
        res.setEnhancerIndex(enhancerIndex);
        res.setDeleted(false);
        return res;
    }

}
