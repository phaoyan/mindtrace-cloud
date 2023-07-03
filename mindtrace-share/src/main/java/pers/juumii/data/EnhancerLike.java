package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerLike {
    private Long id;
    private Long userId;
    private Long enhancerId;
    @TableLogic
    private Boolean deleted;
}
