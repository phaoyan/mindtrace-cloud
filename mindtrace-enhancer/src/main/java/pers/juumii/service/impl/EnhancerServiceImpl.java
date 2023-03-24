package pers.juumii.service.impl;

import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.CheckEnhancerExistence;
import pers.juumii.annotation.CheckUserExistence;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.LabelDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.EnhancerService;
import pers.juumii.utils.SaResult;

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
    public SaResult query(Long userId, Long id) {
        List<Enhancer> enhancers = enhancerMapper.queryEnhancersByUserId(userId);
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
        Opt.of(dto.getCreateBy()).ifPresent(enhancer::setCreateBy);
        Opt.of(dto.getDeleted()).ifPresent(enhancer::setDeleted);
        Opt.of(dto.getCreateTime()).ifPresent(enhancer::setCreateTime);
        Opt.of(dto.getIntroduction()).ifPresent(enhancer::setIntroduction);
        Opt.of(dto.getLength()).ifPresent(enhancer::setLength);
        Opt.of(dto.getPrivacy()).ifPresent(enhancer::setPrivacy);
        Opt.of(dto.getLabels()).ifPresent(labelDTOs->
            enhancer.setLabels(
                labelMapper.selectBatchIds(labelDTOs
                .stream().map(LabelDTO::getName)
                .collect(Collectors.toList()))));
        Opt.of(dto.getResources()).ifPresent(resourceDTOS ->
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
    public SaResult attach(Long enhancerId, Long resourceId) {
        return null;
    }

    @Override
    public SaResult detach(Long enhancerId, Long resourceId) {
        return null;
    }

    @Override
    public SaResult label(Long enhancerId, String labelName) {
        return null;
    }

    @Override
    public SaResult unlabel(Long enhancerId, String labelName) {
        return null;
    }
}
