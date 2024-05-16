package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.data.persistent.TraceKnodeRel;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.mapper.TraceKnodeRelMapper;
import pers.juumii.service.StudyTraceService;
import pers.juumii.service.TraceGroupService;
import pers.juumii.utils.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyTraceServiceImpl implements StudyTraceService {

    private final Neo4jUtils neo4j;
    private final StudyTraceMapper studyTraceMapper;
    private final TraceKnodeRelMapper traceKnodeRelMapper;
    private final TraceEnhancerRelMapper traceEnhancerRelMapper;
    private final TraceGroupService traceGroupService;

    @Autowired
    public StudyTraceServiceImpl(
            Neo4jUtils neo4j,
            StudyTraceMapper studyTraceMapper,
            TraceKnodeRelMapper traceKnodeRelMapper,
            TraceEnhancerRelMapper traceEnhancerRelMapper,
            TraceGroupService traceGroupService) {
        this.neo4j = neo4j;
        this.studyTraceMapper = studyTraceMapper;
        this.traceKnodeRelMapper = traceKnodeRelMapper;
        this.traceEnhancerRelMapper = traceEnhancerRelMapper;
        this.traceGroupService = traceGroupService;
    }

    @Override
    @Transactional
    public StudyTrace addStudyTrace(StudyTraceDTO data) {
        return data.getId() != null ? updateStudyTrace(data) : insertStudyTrace(data);
    }

    @Override
    @Transactional
    public StudyTrace insertStudyTrace(StudyTraceDTO data) {
        StudyTrace trace = StudyTrace.transfer(data);
        studyTraceMapper.insert(trace);
        return trace;
    }

    @Override
    @Transactional
    public StudyTrace updateStudyTrace(StudyTraceDTO data) {
        studyTraceMapper.updateById(StudyTrace.transfer(data));
        return studyTraceMapper.selectById(data.getId());
    }

    @Override
    public List<StudyTrace> getUserStudyTraces(Long userId) {
        if(userId == null)
            userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<StudyTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTrace::getUserId, userId);
        return studyTraceMapper.selectList(wrapper);
    }

    @Override
    public StudyTrace getStudyTrace(Long traceId) {
        return studyTraceMapper.selectById(traceId);
    }

    @Override
    @Transactional
    public void removeStudyTrace(Long traceId) {
        List<Long> traceKnodeRels = getTraceKnodeRels(traceId);
        List<Long> traceEnhancerRels = getTraceEnhancerRels(traceId);
        traceKnodeRels.forEach(knodeId->removeTraceKnodeRel(traceId, knodeId));
        traceEnhancerRels.forEach(enhancerId->removeTraceEnhancerRel(traceId, enhancerId));
        traceGroupService.remove(traceId);
        studyTraceMapper.deleteById(traceId);
    }

    @Override
    @Transactional
    public void addTraceKnodeRel(Long traceId, Long knodeId) {
        if(!checkTraceKnodeRel(traceId, knodeId))
            traceKnodeRelMapper.insert(TraceKnodeRel.prototype(traceId, knodeId));
        Cypher cypher = Cypher.cypher("""
                MERGE (trace: StudyTrace{id: $traceId})
                WITH trace
                MATCH (knode: Knode {id: $knodeId})
                MERGE (knode)-[:HAS_TRACE]->(trace)
                MERGE (trace)-[:HAS_KNODE]->(knode)
                """, Map.of("traceId", traceId, "knodeId", knodeId));
        neo4j.transaction(List.of(cypher));
    }

    @Override
    public List<Long> getTraceKnodeRels(Long traceId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId);
        return traceKnodeRelMapper.selectList(wrapper)
                .stream().map(TraceKnodeRel::getKnodeId)
                .collect(Collectors.toSet()).stream()
                .toList();
    }

    @Override
    public List<IdPair> getTraceKnodeRels(List<Long> traceIds) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TraceKnodeRel::getTraceId, traceIds);
        return traceKnodeRelMapper.selectList(wrapper).stream()
                .map(rel->IdPair.of(rel.getTraceId(), rel.getKnodeId()))
                .collect(Collectors.toSet()).stream()
                .toList();
    }

    @Override
    public List<Long> getTraceEnhancerRels(Long traceId) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceEnhancerRel::getTraceId, traceId);
        return traceEnhancerRelMapper.selectList(wrapper)
                .stream().map(TraceEnhancerRel::getEnhancerId)
                .collect(Collectors.toSet()).stream()
                .toList();
    }

    @Override
    public List<IdPair> getTraceEnhancerRels(List<Long> traceIds) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        if(traceIds.isEmpty()) return new ArrayList<>();
        wrapper.in(TraceEnhancerRel::getTraceId, traceIds);
        return traceEnhancerRelMapper.selectList(wrapper).stream()
                .map(rel->IdPair.of(rel.getTraceId(), rel.getEnhancerId()))
                .collect(Collectors.toSet()).stream()
                .toList();
    }

    @Override
    public Boolean checkTraceKnodeRel(Long traceId, Long knodeId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId).eq(TraceKnodeRel::getKnodeId, knodeId);
        return traceKnodeRelMapper.exists(wrapper);
    }

    @Override
    public List<Long> getStudyTracesOfKnode(Long knodeId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getKnodeId, knodeId);
        return traceKnodeRelMapper.selectList(wrapper)
                .stream().map(TraceKnodeRel::getTraceId)
                .toList();
    }

    @Override
    @Transactional
    public void removeTraceKnodeRel(Long traceId, Long knodeId) {
        Cypher cypher = Cypher.cypher("""
                MATCH (knode: Knode {id: $knodeId})-[r1:HAS_TRACE]->(trace: StudyTrace {id: $traceId}),
                      (trace: StudyTrace {id: $traceId})-[r2:HAS_KNODE]->(knode: Knode {id: $knodeId})
                DELETE r1, r2
                """, Map.of("traceId", traceId, "knodeId", knodeId));
        neo4j.transaction(List.of(cypher));
        LambdaUpdateWrapper<TraceKnodeRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceKnodeRel::getKnodeId, knodeId)
                .eq(TraceKnodeRel::getTraceId, traceId);
        traceKnodeRelMapper.delete(wrapper);
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnodeIncludingBeneath(Long knodeId) {
        Cypher cypher = Cypher.cypher("""
                MATCH (n:Knode {id: $knodeId})
                CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                WITH nodes as all
                UNWIND all AS knode
                MATCH (knode)-[:HAS_TRACE]->(trace)
                RETURN trace.id
                """, Map.of("knodeId", knodeId));
        List<Long> traceIds =
                new HashSet<>(neo4j.session(cypher, (record) ->
                    record.get(0).isNull() ? null :
                    record.get(0).asLong()))
                .stream().toList();
        if(traceIds.isEmpty()) return new ArrayList<>();
        return studyTraceMapper.selectBatchIds(traceIds);
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnodeIncludingBeneathBySlice(Long knodeId, String moment, Integer count) {
        List<StudyTrace> traces = getStudyTracesOfKnodeIncludingBeneath(knodeId);
        traces.sort((a, b)->b.getStartTime().compareTo(a.getStartTime()));
        for(int i = 0; i < traces.size(); i ++)
            if(traces.get(i).getStartTime().isBefore(TimeUtils.parse(moment)))
                return traces.subList(i, Math.min(i + count, traces.size()));
        return new ArrayList<>();
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnodeBatch(List<Long> knodeIds) {
        Cypher cypher = Cypher.cypher("""
                WITH $knodeIds AS knodeIds
                UNWIND knodeIds AS knodeId
                MATCH (knode: Knode {id: knodeId})-[:HAS_TRACE]->(tr: StudyTrace)
                RETURN tr.id
                """, Map.of("knodeIds", knodeIds));
        List<Long> traceIds =
                new HashSet<>(neo4j.session(cypher, (record) ->
                        record.get(0).isNull() ? null :
                                record.get(0).asLong()))
                        .stream().toList();
        if(traceIds.isEmpty()) return new ArrayList<>();
        return studyTraceMapper.selectBatchIds(traceIds);
    }

    @Override
    public List<StudyTrace> getStudyTracesOfEnhancer(Long enhancerId) {
        Cypher cypher = Cypher.cypher("""
                MATCH (enhancer: Enhancer {id:$enhancerId})-[:ENHANCER_TO_TRACE]->(trace: StudyTrace)
                RETURN trace.id
                """, Map.of("enhancerId", enhancerId));
        List<Long> traceIds = neo4j.session(cypher, (record -> record.get(0).isNull() ? null : record.get(0).asLong()));
        traceIds = new HashSet<>(traceIds).stream().toList();
        return traceIds.stream().map(this::getStudyTrace).toList();
    }

    @Override
    public List<Long> getTracedEnhancerIdsFromList(List<Long> enhancerIds){
        if(enhancerIds.isEmpty()) return new ArrayList<>();
        Cypher cypher = Cypher.cypher("""
                WITH $enhancerIds AS enhancerIds
                UNWIND enhancerIds AS enhancerId
                MATCH (enhancer: Enhancer {id: enhancerId})-[:ENHANCER_TO_TRACE]->(trace: StudyTrace)
                RETURN enhancer.id
                """, Map.of("enhancerIds", enhancerIds));
        List<Long> tracedEnhancerIds = neo4j.session(cypher, record -> record.get(0).isNull() ? null : record.get(0).asLong());
        return new HashSet<>(tracedEnhancerIds).stream().toList();
    }

    @Override
    public void removeTraceEnhancerRel(Long traceId, Long enhancerId) {
        Cypher cypher = Cypher.cypher("""
                MATCH (enhancer: Enhancer {id: $enhancerId})-[r1:ENHANCER_TO_TRACE]->(trace: StudyTrace {id: $traceId}),
                (trace: StudyTrace {id: $traceId})-[r2:TRACE_TO_ENHANCER]->(enhancer: Enhancer {id: $enhancerId})
                DELETE r1, r2
                """, Map.of("traceId", traceId, "enhancerId", enhancerId));
        neo4j.transaction(List.of(cypher));
        LambdaUpdateWrapper<TraceEnhancerRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper
                .eq(TraceEnhancerRel::getTraceId, traceId)
                .eq(TraceEnhancerRel::getEnhancerId, enhancerId);
        traceEnhancerRelMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void addTraceEnhancerRel(Long traceId, Long enhancerId) {
        Cypher cypher = Cypher.cypher("""
                MERGE (trace: StudyTrace{id: $traceId})
                WITH trace
                MATCH (enhancer: Enhancer {id: $enhancerId})
                MERGE (enhancer)-[:ENHANCER_TO_TRACE]->(trace)
                MERGE (trace)-[:TRACE_TO_ENHANCER]->(enhancer)
                """, Map.of("traceId", traceId, "enhancerId", enhancerId));
        neo4j.transaction(List.of(cypher));
        if(!checkTraceEnhancerRel(traceId, enhancerId))
            traceEnhancerRelMapper.insert(TraceEnhancerRel.prototype(traceId, enhancerId));
    }

    private Boolean checkTraceEnhancerRel(Long traceId, Long enhancerId) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceEnhancerRel::getEnhancerId, enhancerId)
                .eq(TraceEnhancerRel::getTraceId, traceId);
        return traceEnhancerRelMapper.exists(wrapper);
    }


}
