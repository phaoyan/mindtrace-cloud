package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("enhancer_resource")
public class EnhancerResourceRel {

    private Long enhancerId;
    private Long resourceId;
    @TableLogic
    private Boolean deleted;

    public static EnhancerResourceRel prototype(Long enhancerId, Long resourceId){
        EnhancerResourceRel res = new EnhancerResourceRel();
        res.setEnhancerId(enhancerId);
        res.setResourceId(resourceId);
        res.setDeleted(false);
        return res;
    }

}
