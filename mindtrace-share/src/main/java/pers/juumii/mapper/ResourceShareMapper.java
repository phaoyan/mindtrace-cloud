package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.ResourceShare;

@Mapper
public interface ResourceShareMapper extends BaseMapper<ResourceShare> {
    default void deleteByResourceId(Long resourceId){
        LambdaUpdateWrapper<ResourceShare> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ResourceShare::getResourceId, resourceId);
        delete(wrapper);
    }
}
