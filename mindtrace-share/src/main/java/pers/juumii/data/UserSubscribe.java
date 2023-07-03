package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class UserSubscribe {
    @TableId
    private Long id;
    private Long subscriberId;
    private Long subscriberKnodeId;
    private Long userId;
    @TableLogic
    private Boolean deleted;

    public static UserSubscribe prototype(Long knodeId, Long targetId, Long subscriberId) {
        UserSubscribe res = new UserSubscribe();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setSubscriberId(subscriberId);
        res.setSubscriberKnodeId(knodeId);
        res.setUserId(targetId);
        res.setDeleted(false);
        return res;
    }
}
