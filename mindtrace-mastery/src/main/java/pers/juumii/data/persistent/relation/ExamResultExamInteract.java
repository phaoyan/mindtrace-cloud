package pers.juumii.data.persistent.relation;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_result_interact_rel")
public class ExamResultExamInteract {
    @TableId
    private Long id;
    private Long examResultId;
    private Long examInteractId;
    @TableLogic
    private Boolean deleted;

    public static ExamResultExamInteract prototype(Long examResultId, Long interactId) {
        ExamResultExamInteract res = new ExamResultExamInteract();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setExamResultId(examResultId);
        res.setExamInteractId(interactId);
        res.setDeleted(false);
        return res;
    }
}
