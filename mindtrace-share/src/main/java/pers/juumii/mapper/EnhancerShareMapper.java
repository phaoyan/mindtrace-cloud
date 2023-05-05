package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.EnhancerShare;

import java.util.List;

@Mapper
public interface EnhancerShareMapper extends BaseMapper<EnhancerShare> {
    default void deleteByEnhancerId(Long enhancerId){
        LambdaUpdateWrapper<EnhancerShare> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerShare::getEnhancerId, enhancerId);
        delete(wrapper);
    }

    default Boolean existsByEnhancerId(Long id){
        LambdaQueryWrapper<EnhancerShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerShare::getEnhancerId, id);
        return exists(wrapper);
    }

    default EnhancerShare selectByEnhancerId(Long id){
        LambdaQueryWrapper<EnhancerShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerShare::getEnhancerId, id);
        return selectOne(wrapper);
    }
}
