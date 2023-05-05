package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("enhancer_resource")
public class EnhancerResourceRelationship {

    private Long enhancerId;
    private Long resourceId;
    @TableLogic
    private Boolean deleted;

}
