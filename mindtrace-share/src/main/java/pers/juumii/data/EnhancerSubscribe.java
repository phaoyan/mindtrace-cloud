package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class EnhancerSubscribe {
    @TableId
    private Long id;
    private Long subscriberId;
    private Long subscriberKnodeId;
    private Long enhancerId;
    @TableLogic
    private Boolean deleted;

    public static EnhancerSubscribe prototype(Long knodeId, Long targetId, Long subscriberId) {
        EnhancerSubscribe res = new EnhancerSubscribe();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setSubscriberId(subscriberId);
        res.setSubscriberKnodeId(knodeId);
        res.setEnhancerId(targetId);
        res.setDeleted(false);
        return res;
    }
}
