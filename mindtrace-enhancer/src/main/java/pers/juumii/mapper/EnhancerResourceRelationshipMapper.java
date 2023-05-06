package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.EnhancerResourceRelationship;

import java.util.List;

@Mapper
public interface EnhancerResourceRelationshipMapper extends BaseMapper<EnhancerResourceRelationship> {
    default List<EnhancerResourceRelationship> selectByEnhancerId(Long enhancerId){
        LambdaQueryWrapper<EnhancerResourceRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerResourceRelationship::getEnhancerId, enhancerId);
        return selectList(wrapper);
    }

    default void deleteByResourceId(Long resourceId){
        LambdaUpdateWrapper<EnhancerResourceRelationship> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerResourceRelationship::getResourceId, resourceId);
        delete(wrapper);
    }
}
