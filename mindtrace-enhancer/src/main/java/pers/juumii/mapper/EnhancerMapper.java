package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.juumii.data.Enhancer;

import java.util.List;

@Mapper
public interface EnhancerMapper extends BaseMapper<Enhancer> {

    List<Enhancer> queryByUserId(Long userId);

    List<Enhancer> queryByKnodeId(Long knodeId);

    // 将enhancer与user绑定
    void connectToUser(@Param("userId") Long userId, @Param("enhancerId") Long id);

    // 将enhancer与user解绑
    void disconnectFromUser(@Param("userId") Long userId, @Param("enhancerId") Long enhancerId);

    void label(@Param("enhancerId") Long enhancerId, @Param("labelName") String labelName);

    void unlabel(@Param("enhancerId") Long enhancerId, @Param("labelName") String labelName);

    void connectEnhancerToKnode(@Param("knodeId") Long knodeId, @Param("enhancerId") Long enhancerId);

    void disconnectEnhancerFromKnode(@Param("knodeId") Long knodeId, @Param("enhancerId") Long enhancerId);

    List<Long> queryRelatedKnodeIds(@Param("enhancerId") Long enhancerId);
}
