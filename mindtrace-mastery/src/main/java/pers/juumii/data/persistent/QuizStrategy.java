package pers.juumii.data.persistent;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * 目的：记录评价一个Knode熟练度的练习策略
 */
@Data
public class QuizStrategy {
    @TableId
    private Long id;
    private Long knodeId;
    private Long userId;
    /**
     * json字符串，大致的规则为：
     *  {
     *      type: xxx,
     *      config:{ ... }
     *  }
     *  以实际的解析方式为准
     */
    private String quizStrategy;
    @TableLogic
    private Boolean deleted;

    public static QuizStrategy prototype(Long knodeId, String strategy) {
        QuizStrategy res = new QuizStrategy();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setKnodeId(knodeId);
        res.setUserId(StpUtil.getLoginIdAsLong());
        res.setQuizStrategy(strategy);
        res.setDeleted(false);
        return res;
    }
}
