package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.share.KnodeShareDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.utils.SpringUtils;

import java.util.List;

@Data
public class KnodeShare {
    @TableId
    private Long id;
    private Long knodeId;
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

    public static KnodeShare prototype(Long userId, Long knodeId){
        KnodeShare res = new KnodeShare();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setKnodeId(knodeId);
        res.setUserId(userId);
        res.setVisits(0L);
        res.setLikes(0L);
        res.setFavorites(0L);
        res.setDeleted(false);
        return res;
    }

    public static KnodeShareDTO transfer(KnodeShare knodeShare){
        if(knodeShare == null) return null;
        KnodeShareDTO res = new KnodeShareDTO();
        res.setId(knodeShare.getId().toString());
        res.setKnodeId(knodeShare.getKnodeId().toString());
        res.setUserId(knodeShare.getUserId().toString());
        res.setRate(knodeShare.getRate());
        res.setFavorites(knodeShare.getFavorites());
        res.setVisits(knodeShare.getVisits());
        res.setLikes(knodeShare.getLikes());
        res.setKnode(SpringUtils.getBean(CoreClient.class).check(knodeShare.getKnodeId()));
        return res;
    }

    public static List<KnodeShareDTO> transfer(List<KnodeShare> knodeShares) {
        return knodeShares.stream().map(KnodeShare::transfer).toList();
    }
}
