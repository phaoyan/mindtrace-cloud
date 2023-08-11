package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.utils.SpringUtils;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface EnhancerMapper extends BaseMapper<Enhancer> {

    default List<Enhancer> queryByUserId(Long userId){
        LambdaQueryWrapper<Enhancer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enhancer::getCreateBy, userId);
        return selectList(wrapper);
    }

    default List<Enhancer> queryByKnodeId(Long knodeId){
        EnhancerKnodeRelationshipMapper relMapper = SpringUtils.getBean(EnhancerKnodeRelationshipMapper.class);
        List<EnhancerKnodeRel> rels = relMapper.getByKnodeId(knodeId);
        List<Long> ids = rels.stream().map(EnhancerKnodeRel::getEnhancerId).toList();
        return ids.isEmpty() ? new ArrayList<>() : selectBatchIds(ids);
    }

}
