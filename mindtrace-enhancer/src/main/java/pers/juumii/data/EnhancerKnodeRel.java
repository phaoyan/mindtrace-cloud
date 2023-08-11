package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("enhancer_knode")
public class EnhancerKnodeRel {

    private Long enhancerId;
    private Long knodeId;
    @TableLogic
    private Boolean deleted;
}
