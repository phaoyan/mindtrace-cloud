package pers.juumii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.EnhancerShare;
import pers.juumii.data.KnodeShare;
import pers.juumii.data.ResourceShare;
import pers.juumii.data.UserShare;
import pers.juumii.mapper.EnhancerShareMapper;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.mapper.ResourceShareMapper;
import pers.juumii.mapper.UserShareMapper;
import pers.juumii.service.AuthService;


@Service
public class AuthServiceImpl implements AuthService  {

    private final UserShareMapper userShareMapper;
    private final KnodeShareMapper knodeShareMapper;
    private final EnhancerShareMapper enhancerShareMapper;
    private final ResourceShareMapper resourceShareMapper;

    @Autowired
    public AuthServiceImpl(
            UserShareMapper userShareMapper,
            KnodeShareMapper knodeShareMapper,
            EnhancerShareMapper enhancerShareMapper,
            ResourceShareMapper resourceShareMapper) {
        this.userShareMapper = userShareMapper;
        this.knodeShareMapper = knodeShareMapper;
        this.enhancerShareMapper = enhancerShareMapper;
        this.resourceShareMapper = resourceShareMapper;
    }


    @Override
    public Boolean isUserPublic(Long userId) {
        LambdaQueryWrapper<UserShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserShare::getUserId, userId);
        return userShareMapper.exists(wrapper);
    }

    @Override
    public Boolean isKnodePublic(Long knodeId) {
        LambdaQueryWrapper<KnodeShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnodeShare::getKnodeId, knodeId);
        return knodeShareMapper.exists(wrapper);
    }

    @Override
    public Boolean isEnhancerPublic(Long enhancerId) {
        LambdaQueryWrapper<EnhancerShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerShare::getEnhancerId, enhancerId);
        return enhancerShareMapper.exists(wrapper);
    }

    @Override
    public Boolean isResourcePublic(Long resourceId) {
        LambdaQueryWrapper<ResourceShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceShare::getResourceId, resourceId);
        return resourceShareMapper.exists(wrapper);
    }
}
