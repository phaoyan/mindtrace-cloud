package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.EnhancerResourceRel;

import java.util.List;

@Mapper
public interface EnhancerResourceRelationshipMapper extends BaseMapper<EnhancerResourceRel> {
    default List<EnhancerResourceRel> selectByEnhancerId(Long enhancerId){
        LambdaQueryWrapper<EnhancerResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerResourceRel::getEnhancerId, enhancerId);
        return selectList(wrapper);
    }

    default void deleteByResourceId(Long resourceId){
        LambdaUpdateWrapper<EnhancerResourceRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerResourceRel::getResourceId, resourceId);
        delete(wrapper);
    }
}
