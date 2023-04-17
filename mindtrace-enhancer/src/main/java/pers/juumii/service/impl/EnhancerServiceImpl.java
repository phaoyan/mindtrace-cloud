package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Enhancer;
import pers.juumii.data.Resource;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.service.impl.serializer.QuizcardSerializer;

import java.util.List;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final EnhancerMapper enhancerMapper;
    private final ResourceService resourceService;

    @Autowired
    public EnhancerServiceImpl(
            EnhancerMapper enhancerMapper,
            ResourceService resourceService) {
        this.enhancerMapper = enhancerMapper;
        this.resourceService = resourceService;
    }


    @Override
    public List<Enhancer> getAllEnhancersFromUser(Long userId) {
        return enhancerMapper.queryByUserId(userId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnode(Long knodeId) {
        return enhancerMapper.queryByKnodeId(knodeId);
    }

    @Override
    public Enhancer getEnhancerById(Long enhancerId) {
        return enhancerMapper.selectById(enhancerId);
    }

    @Override
    public Enhancer addEnhancerToUser(Long userId) {
        Enhancer enhancer = Enhancer.prototype(userId);
        enhancerMapper.insert(enhancer);
        enhancerMapper.connectToUser(userId, enhancer.getId());
        return enhancer;
    }

    @Override
    public Enhancer addEnhancerToKnode(Long userId, Long knodeId) {
        Enhancer enhancer = addEnhancerToUser(userId);
        connectEnhancerToKnode(userId, knodeId, enhancer.getId());
        return enhancer;
    }

    @Override
    public SaResult updateEnhancerOfUser(Long userId, Long enhancerId, Enhancer updated) {
        enhancerMapper.updateById(updated);
        return SaResult.ok("Enhancer updated:" + enhancerId);
    }

    @Override
    public void removeEnhancerFromUser(Long userId, Long enhancerId) {
        resourceService.removeAllResourcesFromEnhancer(enhancerId);
        enhancerMapper.deleteById(enhancerId);
        enhancerMapper.disconnectFromUser(userId, enhancerId);
        List<Long> knodeIds = enhancerMapper.queryRelatedKnodeIds(enhancerId);
        knodeIds.forEach(knodeId->enhancerMapper.disconnectEnhancerFromKnode(knodeId, enhancerId));
    }

    @Override
    public SaResult connectToUser(Long userId, Long enhancerId) {
        enhancerMapper.connectToUser(userId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public SaResult disconnectFromUser(Long userId, Long enhancerId) {
        enhancerMapper.disconnectFromUser(userId, enhancerId);
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
    public SaResult connectEnhancerToKnode(Long userId, Long knodeId, Long enhancerId) {
        enhancerMapper.connectEnhancerToKnode(knodeId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public SaResult disconnectEnhancerToKnode(Long userId, Long knodeId, Long enhancerId){
        enhancerMapper.disconnectEnhancerFromKnode(knodeId, enhancerId);
        return SaResult.ok();
    }

    @Override
    public Enhancer addEnhancerWithQuizcardToKnode(Long userId, Long knodeId) {
        Enhancer enhancer = addEnhancerToKnode(userId, knodeId);
        resourceService.addResourceToEnhancer(
                enhancer.getId(),
                Resource.prototype(ResourceType.QUIZCARD, userId),
                QuizcardSerializer.prototype());
        return enhancer;
    }
}
