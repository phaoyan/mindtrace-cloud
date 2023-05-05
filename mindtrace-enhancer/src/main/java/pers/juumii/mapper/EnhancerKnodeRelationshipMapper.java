package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.EnhancerKnodeRelationship;

import java.util.List;

@Mapper
public interface EnhancerKnodeRelationshipMapper extends BaseMapper<EnhancerKnodeRelationship> {

    default List<EnhancerKnodeRelationship> getByEnhancerId(Long enhancerId){
        LambdaQueryWrapper<EnhancerKnodeRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRelationship::getEnhancerId, enhancerId);
        return selectList(wrapper);
    }

    default Integer deleteRelationship(Long enhancerId, Long knodeId){
        LambdaUpdateWrapper<EnhancerKnodeRelationship> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerKnodeRelationship::getEnhancerId, enhancerId)
                .eq(EnhancerKnodeRelationship::getKnodeId, knodeId);
        return delete(wrapper);
    }

    default List<EnhancerKnodeRelationship> getByKnodeId(Long knodeId){
        LambdaQueryWrapper<EnhancerKnodeRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRelationship::getKnodeId, knodeId);
        return selectList(wrapper);
    }
}
