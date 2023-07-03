package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerKnodeRelationship;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.mq.KnodeExchange;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.thread.ThreadUtils;
import pers.juumii.utils.AuthUtils;
import pers.juumii.utils.SerialTimer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final ThreadUtils threadUtils;
    private final AuthUtils authUtils;
    private final CoreClient coreClient;
    private final EnhancerMapper enhancerMapper;
    private final ResourceMapper resourceMapper;
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private ResourceService resourceService;
    private final RabbitTemplate rabbit;

    @Lazy
    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public EnhancerServiceImpl(
            ThreadUtils threadUtils,
            AuthUtils authUtils,
            CoreClient coreClient,
            EnhancerMapper enhancerMapper,
            ResourceMapper resourceMapper,
            EnhancerKnodeRelationshipMapper ekrMapper,
            RabbitTemplate rabbit) {
        this.threadUtils = threadUtils;
        this.authUtils = authUtils;
        this.coreClient = coreClient;
        this.enhancerMapper = enhancerMapper;
        this.resourceMapper = resourceMapper;
        this.ekrMapper = ekrMapper;
        this.rabbit = rabbit;
    }

    @Override
    public List<Enhancer> getAllEnhancers(Long userId) {
        authUtils.auth(userId);
        return enhancerMapper.queryByUserId(userId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnode(Long knodeId) {
        List<Enhancer> res = enhancerMapper.queryByKnodeId(knodeId);
        if(res.isEmpty()) return res;
        authUtils.auth(res.get(0).getCreateBy());
        return res;
    }

    public List<Enhancer> getEnhancersFromKnodeBatch(List<Long> knodeIds){
        if(knodeIds.isEmpty()) return new ArrayList<>();
        LambdaQueryWrapper<EnhancerKnodeRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EnhancerKnodeRelationship::getKnodeId, knodeIds);
        List<EnhancerKnodeRelationship> rel = ekrMapper.selectList(wrapper);
        List<Long> enhancerIds = rel.stream().map(EnhancerKnodeRelationship::getEnhancerId).toList();
        if(enhancerIds.isEmpty()) return new ArrayList<>();
        return enhancerMapper.selectBatchIds(enhancerIds);
    }

    public Long getEnhancerCountFromKnodeBatch(List<Long> knodeIds){
        if(knodeIds == null || knodeIds.isEmpty()) return 0L;
        LambdaQueryWrapper<EnhancerKnodeRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EnhancerKnodeRelationship::getKnodeId, knodeIds);
        return ekrMapper.selectCount(wrapper);
    }

    @Override
    public Enhancer getEnhancerById(Long enhancerId) {
        Enhancer res = enhancerMapper.selectById(enhancerId);
        if(res == null) return null;
        authUtils.auth(res.getCreateBy());
        return res;
    }

    @Override
    public Enhancer addEnhancerToUser(Long userId) {
        authUtils.same(userId);
        Enhancer enhancer = Enhancer.prototype(userId);
        enhancerMapper.insert(enhancer);

        threadUtils.getUserBlockingQueue().add(()->{
            rabbit. convertAndSend(
                    KnodeExchange.KNODE_EVENT_EXCHANGE,
                    KnodeExchange.ROUTING_KEY_ADD_ENHANCER,
                    JSONUtil.toJsonStr(Enhancer.transfer(enhancer)));
        });
        return enhancer;
    }

    @Override
    public Enhancer addEnhancerToKnode(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
        Enhancer enhancer = addEnhancerToUser(Convert.toLong(knode.getCreateBy()));
        connectEnhancerToKnode(knodeId, enhancer.getId());
        return enhancer;
    }

    @Override
    public SaResult updateEnhancer(Long enhancerId, EnhancerDTO updated) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        authUtils.same(enhancer.getCreateBy());
        Opt.ifPresent(updated.getTitle(), enhancer::setTitle);
        Opt.ifPresent(updated.getIntroduction(), enhancer::setIntroduction);
        Opt.ifPresent(updated.getResourceIds(), resourceIds->
            enhancer.setResources(resourceIds.stream().map(resourceMapper::selectById).toList()));
        enhancerMapper.updateById(enhancer);

        threadUtils.getUserBlockingQueue().add(()->{
            rabbit.convertAndSend(
                    KnodeExchange.KNODE_EVENT_EXCHANGE,
                    KnodeExchange.ROUTING_KEY_UPDATE_ENHANCER,
                    JSONUtil.toJsonStr(Enhancer.transfer(enhancer)));
        });
        return SaResult.ok("Enhancer updated:" + enhancerId);
    }

    @Override
    @Transactional
    public void removeEnhancer(Long enhancerId) {
        Enhancer target = enhancerMapper.selectById(enhancerId);
        if(target == null) return;
        authUtils.same(target.getCreateBy());
        resourceService.removeAllResourcesFromEnhancer(enhancerId);
        enhancerMapper.deleteById(enhancerId);
        List<Long> knodeIds =
                ekrMapper.getByEnhancerId(enhancerId).stream()
                .map(EnhancerKnodeRelationship::getKnodeId).toList();
        knodeIds.forEach(knodeId->ekrMapper.deleteRelationship(enhancerId, knodeId));

        threadUtils.getUserBlockingQueue().add(()->{
            rabbit.convertAndSend(
                    KnodeExchange.KNODE_EVENT_EXCHANGE,
                    KnodeExchange.ROUTING_KEY_REMOVE_ENHANCER,
                    enhancerId.toString());
        });
    }

    @Override
    public void connectEnhancerToKnode(Long knodeId, Long enhancerId) {
        // 借用mindtrace-core完成鉴权
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
        EnhancerKnodeRelationship relationship = new EnhancerKnodeRelationship();
        relationship.setEnhancerId(enhancerId);
        relationship.setKnodeId(knodeId);
        ekrMapper.insert(relationship);
    }

    @Override
    public void disconnectEnhancerFromKnode(Long knodeId, Long enhancerId){
        // 借用mindtrace-core完成鉴权
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
        ekrMapper.deleteRelationship(enhancerId, knodeId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId) {
        SerialTimer timer = SerialTimer.timer();
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        timer.logAndRestart();
        List<Enhancer> res = getEnhancersFromKnodeBatch(offsprings.stream().map(knode -> Convert.toLong(knode.getId())).toList());
        timer.logAndRestart();
        return res;
    }

    @Override
    public Long getEnhancerCount(Long knodeId){
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        return getEnhancerCountFromKnodeBatch(offsprings.stream().map(knode->Convert.toLong(knode.getId())).toList());
    }

}
