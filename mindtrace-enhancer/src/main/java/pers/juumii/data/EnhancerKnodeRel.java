package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("enhancer_knode")
public class EnhancerKnodeRel {

    private Long enhancerId;
    private Long knodeId;
    private Integer enhancerIndex;
    @TableLogic
    private Boolean deleted;

    public static EnhancerKnodeRel prototype(Long knodeId, Long enhancerId, int index){
        EnhancerKnodeRel res = new EnhancerKnodeRel();
        res.setKnodeId(knodeId);
        res.setEnhancerId(enhancerId);
        res.setEnhancerIndex(index);
        res.setDeleted(false);
        return res;
    }
}
