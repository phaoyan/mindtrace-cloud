package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.EnhancerKnodeRel;

import java.util.Comparator;
import java.util.List;

@Mapper
public interface EnhancerKnodeRelationshipMapper extends BaseMapper<EnhancerKnodeRel> {

    default List<EnhancerKnodeRel> getByEnhancerId(Long enhancerId){
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRel::getEnhancerId, enhancerId);
        return selectList(wrapper);
    }

    default Integer deleteRelationship(Long enhancerId, Long knodeId){
        LambdaUpdateWrapper<EnhancerKnodeRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerKnodeRel::getEnhancerId, enhancerId)
                .eq(EnhancerKnodeRel::getKnodeId, knodeId);
        return delete(wrapper);
    }

    default List<EnhancerKnodeRel> getByKnodeId(Long knodeId){
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRel::getKnodeId, knodeId);
        return selectList(wrapper).stream()
                .sorted(Comparator.comparingInt(EnhancerKnodeRel::getEnhancerIndex))
                .toList();
    }

    default void updateIndex(Long knodeId, Long enhancerId, Integer index){
        LambdaUpdateWrapper<EnhancerKnodeRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper
                .eq(EnhancerKnodeRel::getKnodeId, knodeId)
                .eq(EnhancerKnodeRel::getEnhancerId, enhancerId);
        update(EnhancerKnodeRel.prototype(knodeId, enhancerId, index), wrapper);
    }
}
