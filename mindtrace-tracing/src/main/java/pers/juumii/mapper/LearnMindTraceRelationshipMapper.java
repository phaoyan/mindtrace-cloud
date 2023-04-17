package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.LearnMindTraceRelationship;

import java.util.List;

@Mapper
public interface LearnMindTraceRelationshipMapper extends BaseMapper<LearnMindTraceRelationship> {

    default List<Long> findRelatedMindtraceIds(Long learningTraceId){
        LambdaQueryWrapper<LearnMindTraceRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearnMindTraceRelationship::getLearningTraceId, learningTraceId);
        List<LearnMindTraceRelationship> rels = selectList(wrapper);
        return rels.stream().map(LearnMindTraceRelationship::getMindtraceId).toList();
    }

    default void deleteByLearningTraceId(Long traceId){
        LambdaQueryWrapper<LearnMindTraceRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearnMindTraceRelationship::getLearningTraceId, traceId);
        delete(wrapper);
    }
}
