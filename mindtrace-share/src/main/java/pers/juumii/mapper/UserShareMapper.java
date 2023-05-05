package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.UserShare;

@Mapper
public interface UserShareMapper extends BaseMapper<UserShare> {


    default UserShare selectByUserId(Long userId){
        LambdaQueryWrapper<UserShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserShare::getUserId, userId);
        return selectOne(wrapper);
    }

    default Boolean userExists(Long userId){
        return selectByUserId(userId) != null;
    }

    default void deleteByUserId(Long userId){
        LambdaUpdateWrapper<UserShare> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserShare::getUserId, userId);
        delete(wrapper);
    }
}
