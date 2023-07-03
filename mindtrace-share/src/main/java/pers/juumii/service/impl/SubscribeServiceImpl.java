package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.*;
import pers.juumii.mapper.*;
import pers.juumii.service.SubscribeService;

import java.util.List;

@Service
public class SubscribeServiceImpl implements SubscribeService {


    private final UserSubscribeMapper userSubscribeMapper;
    private final KnodeSubscribeMapper knodeSubscribeMapper;
    private final EnhancerSubscribeMapper enhancerSubscribeMapper;
    private final UserShareMapper userShareMapper;
    private final KnodeShareMapper knodeShareMapper;
    private final EnhancerShareMapper enhancerShareMapper;

    @Autowired
    public SubscribeServiceImpl(
            UserSubscribeMapper userSubscribeMapper,
            KnodeSubscribeMapper knodeSubscribeMapper,
            EnhancerSubscribeMapper enhancerSubscribeMapper,
            UserShareMapper userShareMapper,
            KnodeShareMapper knodeShareMapper,
            EnhancerShareMapper enhancerShareMapper) {
        this.userSubscribeMapper = userSubscribeMapper;
        this.knodeSubscribeMapper = knodeSubscribeMapper;
        this.enhancerSubscribeMapper = enhancerSubscribeMapper;
        this.userShareMapper = userShareMapper;
        this.knodeShareMapper = knodeShareMapper;
        this.enhancerShareMapper = enhancerShareMapper;
    }


    @Override
    public List<Long> getUserSubscribes(Long knodeId) {
        LambdaQueryWrapper<UserSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscribe::getSubscriberKnodeId, knodeId)
                .eq(UserSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        List<UserSubscribe> userSubscribes = userSubscribeMapper.selectList(wrapper);
        return userSubscribes.stream().map(UserSubscribe::getUserId).toList();
    }

    @Override
    public List<Long> getKnodeSubscribes(Long knodeId) {
        LambdaQueryWrapper<KnodeSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnodeSubscribe::getSubscriberKnodeId, knodeId)
                .eq(KnodeSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        List<KnodeSubscribe> knodeSubscribes = knodeSubscribeMapper.selectList(wrapper);
        return knodeSubscribes.stream().map(KnodeSubscribe::getKnodeId).toList();
    }

    @Override
    public List<Long> getEnhancerSubscribes(Long knodeId) {
        LambdaQueryWrapper<EnhancerSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerSubscribe::getSubscriberKnodeId, knodeId)
                .eq(EnhancerSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        List<EnhancerSubscribe> enhancerSubscribes = enhancerSubscribeMapper.selectList(wrapper);
        return enhancerSubscribes.stream().map(EnhancerSubscribe::getEnhancerId).toList();
    }

    @Override
    @Transactional
    public void subscribeUser(Long knodeId, Long targetId) {
        userSubscribeMapper.insert(UserSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        UserShare userShare = userShareMapper.selectByUserId(targetId);
        userShare.setFavorites(userShare.getFavorites() + 1);
        userShareMapper.updateById(userShare);
    }

    @Override
    @Transactional
    public void subscribeKnode(Long knodeId, Long targetId) {
        knodeSubscribeMapper.insert(KnodeSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        KnodeShare knodeShare = knodeShareMapper.selectByKnodeId(targetId);
        knodeShare.setFavorites(knodeShare.getFavorites() + 1);
        knodeShareMapper.updateById(knodeShare);
    }

    @Override
    @Transactional
    public void subscribeEnhancer(Long knodeId, Long targetId) {
        enhancerSubscribeMapper.insert(EnhancerSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        EnhancerShare enhancerShare = enhancerShareMapper.selectByEnhancerId(targetId);
        enhancerShare.setFavorites(enhancerShare.getFavorites() + 1);
        enhancerShareMapper.updateById(enhancerShare);
    }

    @Override
    @Transactional
    public void removeUserSubscribe(Long knodeId, Long targetId) {
        LambdaUpdateWrapper<UserSubscribe> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserSubscribe::getUserId, targetId)
                .eq(UserSubscribe::getSubscriberKnodeId, knodeId)
                .eq(UserSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        userSubscribeMapper.delete(wrapper);
        UserShare userShare = userShareMapper.selectByUserId(targetId);
        userShare.setFavorites(userShare.getFavorites() - 1);
        userShareMapper.updateById(userShare);
    }

    @Override
    @Transactional
    public void removeKnodeSubscribe(Long knodeId, Long targetId) {
        LambdaUpdateWrapper<KnodeSubscribe> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(KnodeSubscribe::getKnodeId, targetId)
                .eq(KnodeSubscribe::getSubscriberKnodeId, knodeId)
                .eq(KnodeSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        knodeSubscribeMapper.delete(wrapper);
        KnodeShare knodeShare = knodeShareMapper.selectByKnodeId(targetId);
        knodeShare.setFavorites(knodeShare.getFavorites() - 1);
        knodeShareMapper.updateById(knodeShare);
    }

    @Override
    @Transactional
    public void removeEnhancerSubscribe(Long knodeId, Long targetId) {
        LambdaUpdateWrapper<EnhancerSubscribe> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerSubscribe::getEnhancerId, targetId)
                .eq(EnhancerSubscribe::getSubscriberKnodeId, knodeId)
                .eq(EnhancerSubscribe::getSubscriberId, StpUtil.getLoginIdAsLong());
        enhancerSubscribeMapper.delete(wrapper);
        EnhancerShare enhancerShare = enhancerShareMapper.selectByEnhancerId(targetId);
        enhancerShare.setFavorites(enhancerShare.getFavorites() - 1);
        enhancerShareMapper.updateById(enhancerShare);
    }
}
