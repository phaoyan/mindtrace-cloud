package pers.juumii.data.persistent;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class StudyTimeDistribution {
    @TableId
    private Long id;
    private Long knodeId;
    private Long userId;
    private Long seconds;
    @TableLogic
    private Boolean deleted;

    public static StudyTimeDistribution prototype(Long knodeId, Long userId){
        StudyTimeDistribution res = new StudyTimeDistribution();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setKnodeId(knodeId);
        res.setUserId(userId);
        res.setSeconds(0L);
        res.setDeleted(false);
        return res;
    }

}
