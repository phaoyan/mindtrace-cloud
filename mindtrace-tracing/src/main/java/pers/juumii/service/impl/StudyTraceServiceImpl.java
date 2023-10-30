package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.data.persistent.TraceKnodeRel;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.mapper.TraceKnodeRelMapper;
import pers.juumii.service.StudyTraceService;

import java.util.*;

@Service
public class StudyTraceServiceImpl implements StudyTraceService {

    private final StudyTraceMapper studyTraceMapper;
    private final TraceKnodeRelMapper traceKnodeRelMapper;
    private final TraceEnhancerRelMapper traceEnhancerRelMapper;
    private final CoreClient coreClient;

    @Autowired
    public StudyTraceServiceImpl(
            StudyTraceMapper studyTraceMapper,
            TraceKnodeRelMapper traceKnodeRelMapper,
            TraceEnhancerRelMapper traceEnhancerRelMapper,
            CoreClient coreClient) {
        this.studyTraceMapper = studyTraceMapper;
        this.traceKnodeRelMapper = traceKnodeRelMapper;
        this.traceEnhancerRelMapper = traceEnhancerRelMapper;
        this.coreClient = coreClient;
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
    public List<StudyTrace> getAllStudyTraces() {
        return studyTraceMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public StudyTrace getStudyTrace(Long traceId) {
        return studyTraceMapper.selectById(traceId);
    }

    @Override
    @Transactional
    public void removeStudyTrace(Long traceId) {
        studyTraceMapper.deleteById(traceId);
        LambdaUpdateWrapper<TraceKnodeRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId);
        traceKnodeRelMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void postTraceCoverage(Long traceId, Long knodeId) {
        if(!checkTraceKnodeRel(traceId, knodeId))
            traceKnodeRelMapper.insert(TraceKnodeRel.prototype(traceId, knodeId));
    }

    @Override
    public List<TraceKnodeRel> getAllTraceKnodeRels() {
        return traceKnodeRelMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<Long> getTraceKnodeRels(Long traceId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId);
        return traceKnodeRelMapper.selectList(wrapper)
                .stream().map(TraceKnodeRel::getKnodeId)
                .toList();
    }

    @Override
    public List<IdPair> getTraceKnodeRels(List<Long> traceIds) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TraceKnodeRel::getTraceId, traceIds);
        return traceKnodeRelMapper.selectList(wrapper).stream()
                .map(rel->IdPair.of(rel.getTraceId(), rel.getKnodeId()))
                .toList();
    }

    @Override
    public List<TraceEnhancerRel> getAllTraceEnhancerRels() {
        return traceEnhancerRelMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<Long> getTraceEnhancerRels(Long traceId) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceEnhancerRel::getTraceId, traceId);
        return traceEnhancerRelMapper.selectList(wrapper)
                .stream().map(TraceEnhancerRel::getEnhancerId)
                .toList();
    }

    @Override
    public List<IdPair> getTraceEnhancerRels(List<Long> traceIds) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        if(traceIds.isEmpty()) return new ArrayList<>();
        wrapper.in(TraceEnhancerRel::getTraceId, traceIds);
        return traceEnhancerRelMapper.selectList(wrapper).stream()
                .map(rel->IdPair.of(rel.getTraceId(), rel.getEnhancerId()))
                .toList();
    }

    @Override
    public Boolean checkTraceKnodeRel(Long traceId, Long knodeId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId).eq(TraceKnodeRel::getKnodeId, knodeId);
        return traceKnodeRelMapper.exists(wrapper);
    }

    @Override
    public List<Long> getKnodeCoveringTraces(Long knodeId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getKnodeId, knodeId);
        return traceKnodeRelMapper.selectList(wrapper)
                .stream().map(TraceKnodeRel::getTraceId)
                .toList();
    }

    @Override
    @Transactional
    public void removeTraceCoverage(Long traceId, Long knodeId) {
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceKnodeRel::getTraceId, traceId).eq(TraceKnodeRel::getKnodeId, knodeId);
        TraceKnodeRel coverage = traceKnodeRelMapper.selectOne(wrapper);
        if(coverage != null)
            traceKnodeRelMapper.deleteById(coverage);
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnode(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        List<StudyTrace> traces = getUserStudyTraces(Convert.toLong(knode.getCreateBy()));
        List<Long> offspringIds = coreClient.offspringIds(knodeId);
        HashSet<Long> offspringIdSet = new HashSet<>(offspringIds);
        List<StudyTrace> res = new ArrayList<>();
        for(StudyTrace trace: traces)
            for (Long kid: getTraceKnodeRels(trace.getId()))
                if(offspringIdSet.contains(kid)){
                    res.add(trace);
                    break;
                }
        return res;
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnodeBatch(List<Long> knodeIds) {
        if(knodeIds.isEmpty()) return new ArrayList<>();
        LambdaQueryWrapper<TraceKnodeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TraceKnodeRel::getKnodeId, knodeIds);
        List<TraceKnodeRel> rels = traceKnodeRelMapper.selectList(wrapper);
        if(rels.isEmpty()) return new ArrayList<>();
        return studyTraceMapper.selectBatchIds(rels.stream().map(TraceKnodeRel::getTraceId).toList());
    }

    @Override
    public List<StudyTrace> getStudyTracesOfEnhancer(Long enhancerId) {
        LambdaQueryWrapper<TraceEnhancerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceEnhancerRel::getEnhancerId, enhancerId);
        return traceEnhancerRelMapper.selectList(wrapper).stream()
                .map(rel->getStudyTrace(rel.getTraceId()))
                .toList();
    }

    @Override
    @Transactional
    public void addTraceKnodeRel(IdPair traceKnodeRel) {
        traceKnodeRelMapper.insert(TraceKnodeRel.prototype(traceKnodeRel.getLeftId(), traceKnodeRel.getRightId()));
    }

    @Override
    @Transactional
    public void addTraceEnhancerRel(IdPair traceEnhancerRel) {
        traceEnhancerRelMapper.insert(TraceEnhancerRel.prototype(traceEnhancerRel.getLeftId(), traceEnhancerRel.getRightId()));
    }

    @Override
    public void removeTraceEnhancerRel(Long id) {
        traceEnhancerRelMapper.deleteById(id);
    }

    @Override
    public void removeTraceKnodeRel(Long id) {
        traceKnodeRelMapper.deleteById(id);
    }


}
