package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.LearnMindTraceRelationship;
import pers.juumii.data.LearningTrace;
import pers.juumii.data.Mindtrace;
import pers.juumii.dto.LearningTraceDTO;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.dto.TraceInfo;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.LearnMindTraceRelationshipMapper;
import pers.juumii.mapper.LearningTraceMapper;
import pers.juumii.mapper.MindtraceMapper;
import pers.juumii.service.LearningTraceService;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class LearningTraceServiceImpl implements LearningTraceService {

    private final MindtraceMapper mindtraceMapper;
    private final LearningTraceMapper learningTraceMapper;
    private final LearnMindTraceRelationshipMapper relationshipMapper;
    private final CoreClient coreClient;

    @Autowired
    public LearningTraceServiceImpl(
            MindtraceMapper mindtraceMapper,
            LearningTraceMapper learningTraceMapper,
            LearnMindTraceRelationshipMapper relationshipMapper,
            CoreClient coreClient) {
        this.mindtraceMapper = mindtraceMapper;
        this.learningTraceMapper = learningTraceMapper;
        this.relationshipMapper = relationshipMapper;
        this.coreClient = coreClient;
    }

    @Override
    public SaResult startLearning(Long userId, TraceInfo traceInfo) {
        LearningTrace trace = LearningTrace.prototype(userId, Convert.toLong(traceInfo.getData().get("enhancerId")));
        learningTraceMapper.insert(trace);
        return SaResult.data(LearningTraceDTO.transfer(trace));
    }

    @Override
    public SaResult finishLearning(Long userId, TraceInfo traceInfo) {
        Long id = Convert.toLong(traceInfo.getData().get("id"));
        LearningTrace trace = learningTraceMapper.selectById(id);
        trace.setFinishTime(LocalDateTime.now());
        learningTraceMapper.updateById(trace);
        return SaResult.ok();
    }

    @Override
    public LearningTrace pauseLearning(Long userId, TraceInfo traceInfo) {
        Long id = Convert.toLong(traceInfo.getData().get("id"));
        LearningTrace trace = learningTraceMapper.selectById(id);
        String pause = Convert.toStr(traceInfo.getData().get("pause"));
        trace.getPauseList().add(LocalDateTime.parse(pause, TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
        learningTraceMapper.updateById(trace);
        return checkNow(userId);
    }

    @Override
    public LearningTrace continueLearning(Long userId, TraceInfo traceInfo) {
        Long id = Convert.toLong(traceInfo.getData().get("id"));
        LearningTrace trace = learningTraceMapper.selectById(id);
        String _continue = Convert.toStr(traceInfo.getData().get("continue"));
        trace.getContinueList().add(LocalDateTime.parse(_continue, TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
        learningTraceMapper.updateById(trace);
        return checkNow(userId);
    }

    @Override
    public List<Mindtrace> settleLearning(Long userId, TraceInfo traceInfo) {
        finishLearning(userId, traceInfo);
        List<MindtraceDTO> dtos = traceInfo.getDtos();
        return dtos.stream().map(dto -> {
            Mindtrace mindtrace = Mindtrace.prototype(dto);
            LearnMindTraceRelationship relationship =
                LearnMindTraceRelationship.prototype(
                    Convert.toLong(traceInfo.getData().get("id")),
                    mindtrace.getId());
            mindtraceMapper.insert(mindtrace);
            relationshipMapper.insert(relationship);
            return mindtrace;
        }).toList();
    }

    @Override
    public SaResult dropLearning(Long userId, TraceInfo traceInfo) {
        Long id = Convert.toLong(traceInfo.getData().get("id"));
        learningTraceMapper.deleteById(id);
        return SaResult.ok();
    }


    @Override
    public LearningTrace checkNow(Long userId) {
        LearningTrace latest = checkLatest(userId);
        return latest.getFinishTime() == null ? latest : null;
    }

    @Override
    public List<LearningTrace> checkAll(Long userId) {
        LambdaQueryWrapper<LearningTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningTrace::getCreateBy, userId);
        return learningTraceMapper.selectList(wrapper);
    }

    @Override
    public LearningTrace checkLatest(Long userId) {
        List<LearningTrace> traces = checkAll(userId);
        if(traces.isEmpty()) return null;
        traces.sort(Comparator.comparing(LearningTrace::getCreateTime));
        return traces.get(traces.size() - 1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LearningTrace>  checkKnodeRelatedLearningTraces(Long userId, Long knodeId) {
        // 找这个Knode 的 leaves对应的 mindtrace
        LambdaQueryWrapper<Mindtrace> wrapper = new LambdaQueryWrapper<>();
        List<Long> subKnodeIds = new ArrayList<>();
        for(Object data: Convert.toList(coreClient.getKnodeLeaves(userId, knodeId).getData()))
            subKnodeIds.add(Convert.toLong(((Map<String, Object>)data).get("id")));
        // 如果没有收集到任何leaf，说明这个knode本身是leaf，将其加入
        if(subKnodeIds.isEmpty())
            subKnodeIds.add(knodeId);
        wrapper.in(Mindtrace::getKnodeId, subKnodeIds);
        List<Mindtrace> mindtraces = mindtraceMapper.selectList(wrapper);

        // 然后通过这些mindtrace找到learning trace
        LambdaQueryWrapper<LearnMindTraceRelationship> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.in(LearnMindTraceRelationship::getMindtraceId, mindtraces.stream().map(Mindtrace::getId).toList());
        List<LearnMindTraceRelationship> rels = relationshipMapper.selectList(relWrapper);

        // 去重
        HashSet<Long> deduplicateIds = CollectionUtil.newHashSet(
                rels.stream().map(LearnMindTraceRelationship::getLearningTraceId).toList());
        if(deduplicateIds.isEmpty())
            return new ArrayList<>();
        return learningTraceMapper.selectBatchIds(deduplicateIds);
    }

    @Override
    public List<Long> getRelatedKnodeIdsOfLearningTrace(Long learningTraceId) {
        return mindtraceMapper.selectBatchIds(
                relationshipMapper.findRelatedMindtraceIds(learningTraceId))
                .stream().map(Mindtrace::getKnodeId).toList();
    }

    @Override
    public SaResult removeLearningTraceById(Long traceId) {
        mindtraceMapper.deleteBatchIds(relationshipMapper.findRelatedMindtraceIds(traceId));
        relationshipMapper.deleteByLearningTraceId(traceId);
        learningTraceMapper.deleteById(traceId);
        return SaResult.ok();
    }
}
