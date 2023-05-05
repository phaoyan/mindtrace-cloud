package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.KnodeShare;

@Mapper
public interface KnodeShareMapper extends BaseMapper<KnodeShare> {

    default void deleteByKnodeId(Long knodeId){
        LambdaUpdateWrapper<KnodeShare> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(KnodeShare::getKnodeId, knodeId);
        delete(wrapper);
    }

    default KnodeShare selectByKnodeId(Long knodeId){
        LambdaQueryWrapper<KnodeShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnodeShare::getKnodeId, knodeId);
        return selectOne(wrapper);
    }
}
