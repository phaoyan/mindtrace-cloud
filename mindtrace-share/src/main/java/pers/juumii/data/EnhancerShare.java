package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerShare {
    @TableId
    private Long id;
    private Long enhancerId;
    private Long userId;
    // 资源质量评分
    private Double rate;
    // 访问次数
    private Long visits;
    // 点赞次数
    private Long likes;
    // 收藏次数
    private Long favorites;
    @TableLogic
    private Boolean deleted;
}
