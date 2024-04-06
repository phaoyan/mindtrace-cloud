package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerGroupResourceRel {

    private Long groupId;
    private Long resourceId;
    private Integer resourceIndex;
    @TableLogic
    private Boolean deleted;

    public static EnhancerGroupResourceRel prototype(Long groupId, Long resourceId, Integer resourceIndex){
        EnhancerGroupResourceRel res = new EnhancerGroupResourceRel();
        res.setGroupId(groupId);
        res.setResourceId(resourceId);
        res.setResourceIndex(resourceIndex);
        res.setDeleted(false);
        return res;
    }
}
