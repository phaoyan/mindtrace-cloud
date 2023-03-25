package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.LabelDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.EnhancerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final EnhancerMapper enhancerMapper;
    private final LabelMapper labelMapper;
    private final ResourceMapper resourceMapper;

    @Autowired
    public EnhancerServiceImpl(
            EnhancerMapper enhancerMapper,
            LabelMapper labelMapper,
            ResourceMapper resourceMapper) {
        this.enhancerMapper = enhancerMapper;
        this.labelMapper = labelMapper;
        this.resourceMapper = resourceMapper;
    }


    @Override
    @CheckUserExistence
    public SaResult queryByUserId(Long userId, Long enhancerId) {
        List<Enhancer> enhancers = enhancerMapper.queryByUserId(userId);
        return SaResult.data(enhancers);
    }

    @Override
    @CheckEnhancerExistence
    public SaResult queryByKnodeId(Long knodeId, Long enhancerId) {
        List<Enhancer> enhancers = enhancerMapper.queryByKnodeId(knodeId);
        return SaResult.data(enhancers);
    }

    @Override
    @CheckUserExistence
    public SaResult create(Long userId, EnhancerDTO dto) {
        Enhancer enhancer = Enhancer.prototype(dto, userId);
        enhancerMapper.insert(enhancer);
        enhancerMapper.connect(userId, enhancer.getId());
        return SaResult.ok("Enhancer created: " + enhancer.getId());
    }

    @Override
    @CheckUserExistence
    @CheckEnhancerExistence
    public SaResult update(Long userId, Long enhancerId, EnhancerDTO dto) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        Opt.ofNullable(dto.getCreateBy()).ifPresent(enhancer::setCreateBy);
        Opt.ofNullable(dto.getDeleted()).ifPresent(enhancer::setDeleted);
        Opt.ofNullable(dto.getCreateTime()).ifPresent(enhancer::setCreateTime);
        Opt.ofNullable(dto.getIntroduction()).ifPresent(enhancer::setIntroduction);
        Opt.ofNullable(dto.getLength()).ifPresent(enhancer::setLength);
        Opt.ofNullable(dto.getPrivacy()).ifPresent(enhancer::setPrivacy);
        Opt.ofNullable(dto.getLabels()).ifPresent(labelDTOs->
            enhancer.setLabels(
                labelMapper.selectBatchIds(labelDTOs
                .stream().map(LabelDTO::getName)
                .collect(Collectors.toList()))));
        Opt.ofNullable(dto.getResources()).ifPresent(resourceDTOS ->
            enhancer.setResources(
                resourceMapper.selectBatchIds(resourceDTOS
                .stream().map(ResourceDTO::getId)
                .collect(Collectors.toList()))));
        enhancerMapper.updateById(enhancer);
        return SaResult.ok("Enhancer updated:" + enhancerId);
    }

    @Override
    @CheckUserExistence
    @CheckEnhancerExistence
    public SaResult delete(Long userId, Long enhancerId) {
        enhancerMapper.deleteById(enhancerId);
        enhancerMapper.disconnect(userId, enhancerId);
        return SaResult.ok("Enhancer deleted:" + enhancerId);
    }

    @Override
    @CheckUserExistence
    @CheckEnhancerExistence
    public SaResult connect(Long userId, Long enhancerId) {
        enhancerMapper.connect(userId, enhancerId);
        return SaResult.ok();
    }

    @Override
    @CheckUserExistence
    @CheckEnhancerExistence
    public SaResult disconnect(Long userId, Long enhancerId) {
        enhancerMapper.disconnect(userId, enhancerId);
        return SaResult.ok();
    }

    @Override
    @CheckKnodeExistence
    @CheckEnhancerExistence
    public SaResult use(Long knodeId, Long enhancerId) {
        enhancerMapper.use(knodeId, enhancerId);
        return SaResult.ok();
    }

    @Override
    @CheckKnodeExistence
    @CheckEnhancerExistence
    public SaResult drop(Long knodeId, Long enhancerId) {
        enhancerMapper.drop(knodeId, enhancerId);
        return SaResult.ok();
    }

    @Override
    @CheckEnhancerExistence
    @CheckResourceExistence
    public SaResult attach(Long enhancerId, Long resourceId) {
        enhancerMapper.attach(enhancerId, resourceId);
        return SaResult.ok();
    }

    @Override
    @CheckEnhancerExistence
    @CheckResourceExistence
    public SaResult detach(Long enhancerId, Long resourceId) {
        enhancerMapper.detach(enhancerId, resourceId);
        return SaResult.ok();
    }

    @Override
    @CheckEnhancerExistence
    @CheckLabelExistence
    public SaResult label(Long enhancerId, String labelName) {
        enhancerMapper.label(enhancerId, labelName);
        return SaResult.ok();
    }

    @Override
    @CheckEnhancerExistence
    @CheckLabelExistence
    public SaResult unlabel(Long enhancerId, String labelName) {
        enhancerMapper.unlabel(enhancerId, labelName);
        return SaResult.ok();
    }
}
