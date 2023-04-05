package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;
import pers.juumii.feign.GlobalClient;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.EnhancerService;

import java.util.List;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final GlobalClient client;
    private final EnhancerMapper enhancerMapper;
    private final LabelMapper labelMapper;
    private final ResourceMapper resourceMapper;

    @Autowired
    public EnhancerServiceImpl(
            GlobalClient client,
            EnhancerMapper enhancerMapper,
            LabelMapper labelMapper,
            ResourceMapper resourceMapper) {
        this.client = client;
        this.enhancerMapper = enhancerMapper;
        this.labelMapper = labelMapper;
        this.resourceMapper = resourceMapper;
    }


    @Override
    public List<Enhancer> getAllEnhancersFromUser(Long userId) {
        return enhancerMapper.queryByUserId(userId);
    }

    @Override
    public List<Enhancer> queryByKnodeId(Long knodeId) {
        return enhancerMapper.queryByKnodeId(knodeId);
    }

    @Override
    public Enhancer getEnhancerFromUser(Long userId, Long enhancerId) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        if(!enhancer.getCreateBy().equals(userId))
            throw new RuntimeException("Enhancer access failure: " + enhancer + " of user " + userId);
        return enhancer;
    }

    @Override
    public Enhancer addEnhancerToUser(Long userId) {
        Enhancer enhancer = Enhancer.prototype(userId);
        enhancerMapper.insert(enhancer);
        enhancerMapper.connectToUser(userId, enhancer.getId());
        return enhancer;
    }

    @Override
    public SaResult updateEnhancerOfUser(Long userId, Long enhancerId, Enhancer updated) {
        enhancerMapper.updateById(updated);
        return SaResult.ok("Enhancer updated:" + enhancerId);
    }

    @Override
    public SaResult removeEnhancerFromUser(Long userId, Long enhancerId) {
        enhancerMapper.deleteById(enhancerId);
        enhancerMapper.disconnectFromUser(userId, enhancerId);
        List<Long> knodeIds = enhancerMapper.queryRelatedKnodeIds(enhancerId);
        knodeIds.forEach(knodeId->enhancerMapper.disconnectEnhancerToKnode(knodeId, enhancerId));
        return SaResult.ok("Enhancer deleted:" + enhancerId);
    }

    @Override
    public SaResult connect(Long userId, Long enhancerId) {
        enhancerMapper.connectToUser(userId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public SaResult disconnect(Long userId, Long enhancerId) {
        enhancerMapper.disconnectFromUser(userId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public SaResult attach(Long enhancerId, Long resourceId) {
        enhancerMapper.attach(enhancerId, resourceId);
        return SaResult.ok();
    }

    @Override
    public SaResult detach(Long enhancerId, Long resourceId) {
        enhancerMapper.detach(enhancerId, resourceId);
        return SaResult.ok();
    }

    @Override
    public SaResult label(Long enhancerId, String labelName) {
        enhancerMapper.label(enhancerId, labelName);
        return SaResult.ok();
    }

    @Override
    public SaResult unlabel(Long enhancerId, String labelName) {
        enhancerMapper.unlabel(enhancerId, labelName);
        return SaResult.ok();
    }

    @Override
    public SaResult connectEnhancerToKnode(Long userId, Long enhancerId, Long knodeId) {
        enhancerMapper.connectEnhancerToKnode(knodeId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public SaResult disconnectEnhancerToKnode(Long userId, Long enhancerId, Long knodeId){
        enhancerMapper.disconnectEnhancerToKnode(knodeId, enhancerId);
        return SaResult.ok();
    }
}
