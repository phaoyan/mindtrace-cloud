package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.thread.ThreadUtils;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.SerialTimer;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final CoreClient coreClient;
    private final EnhancerMapper enhancerMapper;
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private ResourceService resourceService;
    private final MqClient mqClient;

    @Lazy
    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public EnhancerServiceImpl(
            CoreClient coreClient,
            EnhancerMapper enhancerMapper,
            EnhancerKnodeRelationshipMapper ekrMapper,
            MqClient mqClient) {
        this.mqClient = mqClient;
        this.coreClient = coreClient;
        this.enhancerMapper = enhancerMapper;
        this.ekrMapper = ekrMapper;
    }

    @Override
    public List<Enhancer> getAllEnhancers(Long userId) {
        return enhancerMapper.queryByUserId(userId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnode(Long knodeId) {
        return enhancerMapper.queryByKnodeId(knodeId);
    }

    public List<Enhancer> getEnhancersFromKnodeBatch(List<Long> knodeIds){
        if(knodeIds.isEmpty()) return new ArrayList<>();
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EnhancerKnodeRel::getKnodeId, knodeIds);
        List<EnhancerKnodeRel> rel = ekrMapper.selectList(wrapper);
        List<Long> enhancerIds = rel.stream().map(EnhancerKnodeRel::getEnhancerId).toList();
        if(enhancerIds.isEmpty()) return new ArrayList<>();
        return enhancerMapper.selectBatchIds(enhancerIds);
    }


    @Override
    public List<KnodeDTO> getKnodeByEnhancerId(Long enhancerId) {
        List<EnhancerKnodeRel> rels = ekrMapper.getByEnhancerId(enhancerId);
        if(rels.isEmpty()) removeEnhancer(enhancerId);
        return coreClient.checkBatch(rels.stream().map(EnhancerKnodeRel::getKnodeId).toList());
    }

    @Override
    public List<IdPair> getKnodeEnhancerRels(List<Long> knodeIds) {
        return knodeIds.stream()
                .map(ekrMapper::getByKnodeId)
                .flatMap(Collection::stream)
                .map(rel->IdPair.of(rel.getKnodeId().toString(), rel.getEnhancerId().toString()))
                .toList();
    }

    @Override
    public Enhancer getEnhancerById(Long enhancerId) {
        return enhancerMapper.selectById(enhancerId);
    }

    @Override
    public List<Enhancer> getEnhancersByDate(Long userId, String left, String right) {
        if(userId == null) userId = StpUtil.getLoginIdAsLong();
        if(StrUtil.isBlank(left)) left = TimeUtils.format(LocalDateTime.of(2000,1,1,0,0));
        if(StrUtil.isBlank(right)) right = TimeUtils.format(LocalDateTime.now());
        LambdaQueryWrapper<Enhancer> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Enhancer::getCreateBy, userId)
                .ge(Enhancer::getCreateTime, left)
                .le(Enhancer::getCreateTime, right);
        return enhancerMapper.selectList(wrapper);
    }

    @Override
    public List<Enhancer> getEnhancersByDateBeneathKnode(Long knodeId, String left, String right){
        KnodeDTO knode = coreClient.check(knodeId);
        Long userId = Convert.toLong(knode.getCreateBy());
        List<Enhancer> enhancers = getEnhancersByDate(userId, left, right);
        return enhancers.stream().filter(enhancer->{
            List<EnhancerKnodeRel> rels = ekrMapper.getByEnhancerId(enhancer.getId());
            return DataUtils.ifAny(rels, rel->coreClient.isOffspring(rel.getKnodeId(), knodeId));
        }).toList();
    }

    @Override
    public List<Long> getKnodeIdsWithQuiz(Long rootId) {
        return coreClient.offsprings(rootId).stream()
                .map(knode->Convert.toLong(knode.getId()))
                .filter(knodeId->DataUtils.ifAny(getEnhancersFromKnode(knodeId), Enhancer::getIsQuiz))
                .toList();
    }

    @Override
    @Transactional
    public Enhancer addEnhancerToUser(Long userId) {
        Enhancer enhancer = Enhancer.prototype(userId);
        enhancerMapper.insert(enhancer);
        mqClient.emit(MessageEvents.ADD_ENHANCER, JSONUtil.toJsonStr(Enhancer.transfer(enhancer)));
        return enhancer;
    }

    @Override
    @Transactional
    public Enhancer addEnhancerToKnode(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        Enhancer enhancer = addEnhancerToUser(Convert.toLong(knode.getCreateBy()));
        connectEnhancerToKnode(knodeId, enhancer.getId());
        return enhancer;
    }

    @Override
    @Transactional
    public void updateEnhancer(Long enhancerId, EnhancerDTO updated) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        Opt.ifPresent(updated.getTitle(), enhancer::setTitle);
        Opt.ifPresent(updated.getIntroduction(), enhancer::setIntroduction);
        Opt.ifPresent(updated.getIsQuiz(), enhancer::setIsQuiz);
        enhancerMapper.updateById(enhancer);
    }

    @Override
    @Transactional
    public void setIsQuiz(Long enhancerId, Boolean isQuiz) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        enhancer.setIsQuiz(isQuiz);
        enhancerMapper.updateById(enhancer);
    }

    @Override
    @Transactional
    public void setTitle(Long enhancerId, String title) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        enhancer.setTitle(title);
        enhancerMapper.updateById(enhancer);
    }

    @Override
    @Transactional
    public void removeEnhancer(Long enhancerId) {
        Enhancer target = enhancerMapper.selectById(enhancerId);
        if(target == null) return;
        resourceService.removeAllResourcesFromEnhancer(enhancerId);
        enhancerMapper.deleteById(enhancerId);
        List<Long> knodeIds =
                ekrMapper.getByEnhancerId(enhancerId).stream()
                .map(EnhancerKnodeRel::getKnodeId).toList();
        knodeIds.forEach(knodeId->ekrMapper.deleteRelationship(enhancerId, knodeId));
        mqClient.emit(MessageEvents.REMOVE_ENHANCER, enhancerId.toString());
    }

    @Override
    @Transactional
    public void connectEnhancerToKnode(Long knodeId, Long enhancerId) {
        EnhancerKnodeRel relationship = new EnhancerKnodeRel();
        relationship.setEnhancerId(enhancerId);
        relationship.setKnodeId(knodeId);
        ekrMapper.insert(relationship);
    }

    @Override
    @Transactional
    public void disconnectEnhancerFromKnode(Long knodeId, Long enhancerId){
        ekrMapper.deleteRelationship(enhancerId, knodeId);
        if(ekrMapper.getByEnhancerId(enhancerId).size() == 0)
            removeEnhancer(enhancerId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId) {
        return getEnhancersFromKnodeBatch(coreClient.offspringIds(knodeId));
    }

    @Override
    public List<Long> getEnhancerIdsFromKnodeIncludingBeneath(Long knodeId){
        return getEnhancersFromKnodeIncludingBeneath(knodeId)
                .stream().map(Enhancer::getId)
                .toList();
    }


    @Override
    public Long getEnhancerCount(Long knodeId){
        List<Long> offspringIds = coreClient.offspringIds(knodeId);
        if(offspringIds.isEmpty()) return 0L;
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EnhancerKnodeRel::getKnodeId, offspringIds);
        return ekrMapper.selectCount(wrapper);
    }

}
