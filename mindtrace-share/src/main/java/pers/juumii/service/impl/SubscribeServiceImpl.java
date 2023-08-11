package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.*;
import pers.juumii.mapper.*;
import pers.juumii.service.EnhancerShareService;
import pers.juumii.service.KnodeShareService;
import pers.juumii.service.SubscribeService;
import pers.juumii.service.UserShareService;

import java.util.List;

@Service
public class SubscribeServiceImpl implements SubscribeService {


    private final UserSubscribeMapper userSubscribeMapper;
    private final KnodeSubscribeMapper knodeSubscribeMapper;
    private final EnhancerSubscribeMapper enhancerSubscribeMapper;
    private final UserShareMapper userShareMapper;
    private final KnodeShareMapper knodeShareMapper;
    private final EnhancerShareMapper enhancerShareMapper;
    private final UserShareService userShareService;
    private final KnodeShareService knodeShareService;
    private final EnhancerShareService enhancerShareService;

    @Autowired
    public SubscribeServiceImpl(
            UserSubscribeMapper userSubscribeMapper,
            KnodeSubscribeMapper knodeSubscribeMapper,
            EnhancerSubscribeMapper enhancerSubscribeMapper,
            UserShareMapper userShareMapper,
            KnodeShareMapper knodeShareMapper,
            EnhancerShareMapper enhancerShareMapper,
            UserShareService userShareService,
            KnodeShareService knodeShareService,
            EnhancerShareService enhancerShareService) {
        this.userSubscribeMapper = userSubscribeMapper;
        this.knodeSubscribeMapper = knodeSubscribeMapper;
        this.enhancerSubscribeMapper = enhancerSubscribeMapper;
        this.userShareMapper = userShareMapper;
        this.knodeShareMapper = knodeShareMapper;
        this.enhancerShareMapper = enhancerShareMapper;
        this.userShareService = userShareService;
        this.knodeShareService = knodeShareService;
        this.enhancerShareService = enhancerShareService;
    }


    @Override
    public List<Long> getUserSubscribes(Long knodeId) {
        LambdaQueryWrapper<UserSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscribe::getSubscriberKnodeId, knodeId);
        List<UserSubscribe> userSubscribes = userSubscribeMapper.selectList(wrapper);
        return userSubscribes.stream().map(UserSubscribe::getUserId).toList();
    }

    @Override
    public List<Long> getKnodeSubscribes(Long knodeId) {
        LambdaQueryWrapper<KnodeSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnodeSubscribe::getSubscriberKnodeId, knodeId);
        List<KnodeSubscribe> knodeSubscribes = knodeSubscribeMapper.selectList(wrapper);
        return knodeSubscribes.stream().map(KnodeSubscribe::getKnodeId).toList();
    }

    @Override
    public List<Long> getEnhancerSubscribes(Long knodeId) {
        LambdaQueryWrapper<EnhancerSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerSubscribe::getSubscriberKnodeId, knodeId);
        List<EnhancerSubscribe> enhancerSubscribes = enhancerSubscribeMapper.selectList(wrapper);
        return enhancerSubscribes.stream().map(EnhancerSubscribe::getEnhancerId).toList();
    }

    @Override
    @Transactional
    public void subscribeUser(Long knodeId, Long targetId) {
        LambdaQueryWrapper<UserSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscribe::getSubscriberKnodeId, knodeId).eq(UserSubscribe::getUserId, targetId);
        if(userSubscribeMapper.exists(wrapper)) return;
        userSubscribeMapper.insert(UserSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        UserShare userShare = userShareService.getUserShare(targetId);
        userShare.setFavorites(userShare.getFavorites() + 1);
        userShareMapper.updateById(userShare);
    }

    @Override
    @Transactional
    public void subscribeKnode(Long knodeId, Long targetId) {
        LambdaQueryWrapper<KnodeSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnodeSubscribe::getSubscriberKnodeId, knodeId).eq(KnodeSubscribe::getKnodeId, targetId);
        if(knodeSubscribeMapper.exists(wrapper)) return;
        knodeSubscribeMapper.insert(KnodeSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        KnodeShare knodeShare = knodeShareService.getKnodeShare(targetId);
        knodeShare.setFavorites(knodeShare.getFavorites() + 1);
        knodeShareMapper.updateById(knodeShare);
    }

    @Override
    @Transactional
    public void subscribeEnhancer(Long knodeId, Long targetId) {
        LambdaQueryWrapper<EnhancerSubscribe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerSubscribe::getSubscriberKnodeId, knodeId).eq(EnhancerSubscribe::getEnhancerId, targetId);
        if(enhancerSubscribeMapper.exists(wrapper)) return;
        enhancerSubscribeMapper.insert(EnhancerSubscribe.prototype(knodeId, targetId, StpUtil.getLoginIdAsLong()));
        EnhancerShare enhancerShare = enhancerShareService.getEnhancerShare(targetId);
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
        UserShare userShare = userShareService.getUserShare(targetId);
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
        KnodeShare knodeShare = knodeShareService.getKnodeShare(targetId);
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
        EnhancerShare enhancerShare = enhancerShareService.getEnhancerShare(targetId);
        enhancerShare.setFavorites(enhancerShare.getFavorites() - 1);
        enhancerShareMapper.updateById(enhancerShare);
    }
}
