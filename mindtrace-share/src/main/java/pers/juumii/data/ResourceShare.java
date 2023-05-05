package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.List;

@Data
public class ResourceShare {
    @TableId
    private Long id;
    private Long resourceId;
    private Long userId;
    // 资源质量评分
    private Double rate;
    // 访问次数
    private Long visits;
    // 点赞次数
    private Long likes;
    // 收藏次数
    private Long favorites;
    private List<Comment> comments;
    @TableLogic
    private Boolean deleted;

    public static ResourceShare prototype(Long userId, Long resourceId){
        ResourceShare res = new ResourceShare();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setResourceId(resourceId);
        res.setUserId(userId);
        res.setVisits(0L);
        res.setLikes(0L);
        res.setFavorites(0L);
        res.setDeleted(false);
        return res;
    }
}
