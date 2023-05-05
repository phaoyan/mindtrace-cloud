package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.share.EnhancerShareDTO;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.utils.SpringUtils;

import java.util.List;

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

    public static EnhancerShare prototype(Long userId, Long enhancerId){
        EnhancerShare res = new EnhancerShare();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setEnhancerId(enhancerId);
        res.setUserId(userId);
        res.setVisits(0L);
        res.setLikes(0L);
        res.setFavorites(0L);
        res.setDeleted(false);
        return res;
    }

    public static EnhancerShareDTO transfer(EnhancerShare share){
        EnhancerShareDTO res = new EnhancerShareDTO();
        res.setId(share.getId().toString());
        res.setEnhancerId(share.getEnhancerId().toString());
        res.setUserId(share.getUserId().toString());
        res.setRate(share.getRate());
        res.setFavorites(share.getFavorites());
        res.setLikes(share.getLikes());
        res.setVisits(share.getVisits());
        res.setEnhancer(SpringUtils.getBean(EnhancerClient.class).getEnhancerById(share.getEnhancerId()));
        return res;
    }

    public static List<EnhancerShareDTO> transfer(List<EnhancerShare> shares){
        return shares.stream().map(EnhancerShare::transfer).toList();
    }
}
