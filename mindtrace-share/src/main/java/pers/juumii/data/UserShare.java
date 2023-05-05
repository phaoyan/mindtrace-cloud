package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.share.UserShareDTO;

@Data
public class UserShare {
    @TableId
    private Long id;
    private Long userId;
    private Long visits;
    private Long likes;
    private Long favorites;
    @TableLogic
    private Boolean deleted;

    public static UserShare prototype(Long userId) {
        UserShare res = new UserShare();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setUserId(userId);
        res.setVisits(0L);
        res.setLikes(0L);
        res.setFavorites(0L);
        res.setDeleted(false);
        return res;
    }

    public static UserShareDTO transfer(UserShare userShare) {
        if(userShare == null) return null;

        UserShareDTO res = new UserShareDTO();
        res.setId(userShare.getId().toString());
        res.setUserId(userShare.getUserId().toString());
        res.setFavorites(userShare.getFavorites());
        res.setLikes(userShare.getLikes());
        res.setVisits(userShare.getVisits());
        return res;
    }
}
