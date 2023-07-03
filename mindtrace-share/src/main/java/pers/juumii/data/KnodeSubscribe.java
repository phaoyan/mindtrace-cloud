package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class KnodeSubscribe {
    @TableId
    private Long id;
    private Long subscriberId;
    private Long subscriberKnodeId;
    private Long knodeId;
    @TableLogic
    private Boolean deleted;

    public static KnodeSubscribe prototype(Long knodeId, Long targetId, Long subscriberId) {
        KnodeSubscribe res = new KnodeSubscribe();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setSubscriberId(subscriberId);
        res.setSubscriberKnodeId(knodeId);
        res.setKnodeId(targetId);
        res.setDeleted(false);
        return res;
    }
}
