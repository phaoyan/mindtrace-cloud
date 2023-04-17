package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "learn_mind_trace")
public class LearnMindTraceRelationship {

    private Long learningTraceId;
    private Long mindtraceId;
    @TableLogic
    private Boolean deleted;

    public static LearnMindTraceRelationship prototype(Long learningTraceId, Long mindtraceId){
        LearnMindTraceRelationship res = new LearnMindTraceRelationship();
        res.setLearningTraceId(learningTraceId);
        res.setMindtraceId(mindtraceId);
        res.setDeleted(false);
        return res;
    }
}
