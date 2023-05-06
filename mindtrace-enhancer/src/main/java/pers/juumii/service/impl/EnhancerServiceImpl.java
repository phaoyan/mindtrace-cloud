package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import pers.juumii.utils.AuthUtils;

import java.util.List;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final AuthUtils authUtils;
    private final CoreClient coreClient;
    private final EnhancerMapper enhancerMapper;
    private final ResourceMapper resourceMapper;
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final ResourceService resourceService;
    private final RabbitTemplate rabbit;

    @Autowired
    public EnhancerServiceImpl(
            AuthUtils authUtils,
            CoreClient coreClient,
            EnhancerMapper enhancerMapper,
            ResourceMapper resourceMapper,
            EnhancerKnodeRelationshipMapper ekrMapper,
            ResourceService resourceService,
            RabbitTemplate rabbit) {
        this.authUtils = authUtils;
        this.coreClient = coreClient;
        this.enhancerMapper = enhancerMapper;
        this.resourceMapper = resourceMapper;
        this.ekrMapper = ekrMapper;
        this.resourceService = resourceService;
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

        rabbit.convertAndSend(
                KnodeExchange.KNODE_EVENT_EXCHANGE,
                KnodeExchange.ROUTING_KEY_ADD_ENHANCER,
                JSONUtil.toJsonStr(Enhancer.transfer(enhancer)));
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

        rabbit.convertAndSend(
                KnodeExchange.KNODE_EVENT_EXCHANGE,
                KnodeExchange.ROUTING_KEY_UPDATE_ENHANCER,
                JSONUtil.toJsonStr(Enhancer.transfer(enhancer)));
        return SaResult.ok("Enhancer updated:" + enhancerId);
    }

    @Override
    public void removeEnhancer(Long enhancerId) {
        Enhancer target = enhancerMapper.selectById(enhancerId);
        authUtils.same(target.getCreateBy());
        resourceService.removeAllResourcesFromEnhancer(enhancerId);
        enhancerMapper.deleteById(enhancerId);
        List<Long> knodeIds =
                ekrMapper.getByEnhancerId(enhancerId).stream()
                .map(EnhancerKnodeRelationship::getKnodeId).toList();
        knodeIds.forEach(knodeId->ekrMapper.deleteRelationship(enhancerId, knodeId));

        rabbit.convertAndSend(
                KnodeExchange.KNODE_EVENT_EXCHANGE,
                KnodeExchange.ROUTING_KEY_REMOVE_ENHANCER,
                enhancerId.toString());
    }

    @Override
    public SaResult connectEnhancerToKnode(Long knodeId, Long enhancerId) {
        EnhancerKnodeRelationship relationship = new EnhancerKnodeRelationship();
        relationship.setEnhancerId(enhancerId);
        relationship.setKnodeId(knodeId);
        return SaResult.data(ekrMapper.insert(relationship));
    }

    @Override
    public SaResult disconnectEnhancerFromKnode(Long knodeId, Long enhancerId){
        return SaResult.data(ekrMapper.deleteRelationship(enhancerId, knodeId));
    }

}
