package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerGroupRel;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.data.EnhancerResourceRel;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.MqClient;
import pers.juumii.mapper.*;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.Cypher;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.Neo4jUtils;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnhancerServiceImpl implements EnhancerService {

    private final CoreClient coreClient;
    private final EnhancerMapper enhancerMapper;
    private final EnhancerResourceRelationshipMapper errMapper;
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final EnhancerGroupRelMapper enhancerGroupRelMapper;
    private ResourceService resourceService;
    private final MqClient mqClient;
    private final Neo4jUtils neo4j;


    @Lazy
    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public EnhancerServiceImpl(
            CoreClient coreClient,
            EnhancerMapper enhancerMapper,
            EnhancerResourceRelationshipMapper errMapper,
            EnhancerKnodeRelationshipMapper ekrMapper,
            EnhancerGroupRelMapper enhancerGroupRelMapper,
            MqClient mqClient, Neo4jUtils neo4j) {
        this.errMapper = errMapper;
        this.enhancerGroupRelMapper = enhancerGroupRelMapper;
        this.mqClient = mqClient;
        this.coreClient = coreClient;
        this.enhancerMapper = enhancerMapper;
        this.ekrMapper = ekrMapper;
        this.neo4j = neo4j;
    }

    @Override
    public List<Enhancer> getAllEnhancers(Long userId) {
        return enhancerMapper.queryByUserId(userId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnode(Long knodeId) {
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRel::getKnodeId, knodeId);
        List<EnhancerKnodeRel> rels = ekrMapper.selectList(wrapper);
        return rels.stream()
                .sorted(Comparator.comparing(EnhancerKnodeRel::getEnhancerIndex))
                .map(rel->enhancerMapper.selectById(rel.getEnhancerId()))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Enhancer> getEnhancersFromKnodeBatch(List<Long> knodeIds){
        Cypher cypher = Cypher.cypher("""
                WITH $knodeIds AS knodeIds
                UNWIND knodeIds AS knodeId
                MATCH (knode: Knode {id: knodeId})-[:KNODE_TO_ENHANCER]->(enhancer: Enhancer)
                RETURN enhancer.id
                """, Map.of("knodeIds", knodeIds));
        List<Long> enhancerId =
                new HashSet<>(neo4j.session(cypher, (record) ->
                        record.get(0).isNull() ? null :
                        record.get(0).asLong()))
                        .stream().toList();
        if(enhancerId.isEmpty()) return new ArrayList<>();
        return enhancerMapper.selectBatchIds(enhancerId);
    }


    @Override
    public List<KnodeDTO> getKnodeByEnhancerId(Long enhancerId) {
        List<EnhancerKnodeRel> rels = ekrMapper.getByEnhancerId(enhancerId);
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
    public void setEnhancerIndexInKnode(Long knodeId, Long enhancerId, Integer index) {
        List<EnhancerKnodeRel> rels = new ArrayList<>(ekrMapper.getByKnodeId(knodeId));
        if(index < 0 || index >= rels.size()) return;
        rels.sort(Comparator.comparingInt(EnhancerKnodeRel::getEnhancerIndex));
        Integer oriIndex = ListUtil.lastIndexOf(rels, (rel) -> rel.getEnhancerId().equals(enhancerId));
        if(oriIndex > index){
            for (Integer i = 0; i < rels.size(); i ++){
                if(index <= i && i < oriIndex)
                    rels.get(i).setEnhancerIndex(i+1);
                else if(i.equals(oriIndex))
                    rels.get(i).setEnhancerIndex(index);
                LambdaUpdateWrapper<EnhancerKnodeRel> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(EnhancerKnodeRel::getKnodeId, rels.get(i).getKnodeId())
                        .eq(EnhancerKnodeRel::getEnhancerId, rels.get(i).getEnhancerId());
                ekrMapper.update(rels.get(i), wrapper);
            }
        }else if(oriIndex < index) {
            for (Integer i = 0; i < rels.size(); i ++){
                if(i.equals(oriIndex))
                    rels.get(i).setEnhancerIndex(index);
                else if(oriIndex < i && i <= index)
                    rels.get(i).setEnhancerIndex(i-1);
                LambdaUpdateWrapper<EnhancerKnodeRel> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(EnhancerKnodeRel::getKnodeId, rels.get(i).getKnodeId())
                        .eq(EnhancerKnodeRel::getEnhancerId, rels.get(i).getEnhancerId());
                ekrMapper.update(rels.get(i), wrapper);
            }
        }
    }

    @Override
    public List<Enhancer> getEnhancersByResourceId(Long resourceId) {
        LambdaQueryWrapper<EnhancerResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerResourceRel::getResourceId, resourceId);
        List<EnhancerResourceRel> rels = errMapper.selectList(wrapper);
        return rels.stream().map((rel)->getEnhancerById(rel.getEnhancerId())).toList();
    }

    @Override
    public List<Enhancer> getEnhancersByLike(Long userId, String txt) {
        if(StrUtil.isBlank(txt)) return new ArrayList<>();
        LambdaQueryWrapper<Enhancer> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Enhancer::getCreateBy, userId)
                .like(Enhancer::getTitle, txt);
        return enhancerMapper.selectList(wrapper);
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
        addKnodeEnhancerRel(knodeId, enhancer.getId());
        return enhancer;
    }

    @Override
    @Transactional
    public void updateEnhancer(Long enhancerId, EnhancerDTO updated) {
        Enhancer enhancer = enhancerMapper.selectById(enhancerId);
        Opt.ifPresent(updated.getTitle(), enhancer::setTitle);
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
        List<Long> knodeIds = ekrMapper.getByEnhancerId(enhancerId).stream().map(EnhancerKnodeRel::getKnodeId).toList();
        LambdaUpdateWrapper<EnhancerGroupRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EnhancerGroupRel::getEnhancerId, enhancerId);
        enhancerGroupRelMapper.delete(wrapper);
        knodeIds.forEach(knodeId-> removeKnodeEnhancerRel(knodeId, enhancerId));
        mqClient.emit(MessageEvents.REMOVE_ENHANCER, enhancerId.toString());
    }

    @Override
    @Transactional
    public void addKnodeEnhancerRel(Long knodeId, Long enhancerId) {
        Cypher cypher = Cypher.cypher("""
                MERGE (enhancer: Enhancer{id: $enhancerId})
                WITH enhancer
                MATCH (knode: Knode {id: $knodeId})
                MERGE (knode)-[:KNODE_TO_ENHANCER]->(enhancer)
                MERGE (enhancer)-[:ENHANCER_TO_KNODE]->(knode)
                """, Map.of("enhancerId", enhancerId, "knodeId", knodeId));
        neo4j.transaction(List.of(cypher));
        if(checkKnodeEnhancerRel(knodeId, enhancerId)) return;
        EnhancerKnodeRel relationship = EnhancerKnodeRel.prototype(knodeId, enhancerId, getEnhancersFromKnode(knodeId).size());
        ekrMapper.insert(relationship);
    }

    private Boolean checkKnodeEnhancerRel(Long knodeId, Long enhancerId) {
        LambdaQueryWrapper<EnhancerKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnhancerKnodeRel::getKnodeId, knodeId)
                .eq(EnhancerKnodeRel::getEnhancerId, enhancerId);
        return ekrMapper.exists(wrapper);
    }

    @Override
    @Transactional
    public void removeKnodeEnhancerRel(Long knodeId, Long enhancerId){
        Cypher cypher = Cypher.cypher("""
                MATCH (knode: Knode {id: $knodeId})-[r1:KNODE_TO_ENHANCER]->(enhancer: Enhancer {id: $enhancerId}),
                      (enhancer: Enhancer {id: $enhancerId})-[r2:ENHANCER_TO_KNODE]->(knode: Knode {id: $knodeId})
                DELETE r1, r2
                """, Map.of("enhancerId", enhancerId, "knodeId", knodeId));
        neo4j.transaction(List.of(cypher));
        ekrMapper.deleteRelationship(enhancerId, knodeId);
        if(ekrMapper.getByEnhancerId(enhancerId).size() == 0)
            removeEnhancer(enhancerId);
    }

    @Override
    public List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId) {
        Cypher cypher = Cypher.cypher("""
                MATCH (n:Knode {id: $knodeId})
                CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                WITH nodes as all
                UNWIND all AS knode
                MATCH (knode)-[:KNODE_TO_ENHANCER]->(enhancer)
                RETURN enhancer.id
                """, Map.of("knodeId", knodeId));
        Set<Long> enhancerIds = new HashSet<>(neo4j.session(cypher, (record) ->
                record.get(0).isNull() ? null :
                record.get(0).asLong()));
        if(enhancerIds.isEmpty()) return new ArrayList<>();
        return enhancerMapper.selectBatchIds(enhancerIds);
    }

    @Override
    public List<Long> getEnhancerIdsFromKnodeIncludingBeneath(Long knodeId){
        return getEnhancersFromKnodeIncludingBeneath(knodeId).stream().map(Enhancer::getId).toList();
    }


    @Override
    public Long getEnhancerCount(Long knodeId){
        return Convert.toLong(getEnhancersFromKnodeIncludingBeneath(knodeId).size());
    }

}
