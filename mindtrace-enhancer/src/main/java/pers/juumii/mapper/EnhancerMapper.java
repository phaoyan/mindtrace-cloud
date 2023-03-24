package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.juumii.data.Enhancer;

import java.util.List;

@Mapper
public interface EnhancerMapper extends BaseMapper<Enhancer> {

    List<Enhancer> queryEnhancersByUserId(Long userId);

    // 将enhancer与user绑定
    void connect(@Param("userId") Long userId, @Param("enhancerId") Long id);

    // 将enhancer与user解绑
    void disconnect(@Param("userId") Long userId, @Param("enhancerId") Long enhancerId);
}
