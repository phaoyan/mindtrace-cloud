package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.tracing.StudyTraceEnhancerInfo;
import pers.juumii.dto.tracing.StudyTraceKnodeInfo;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.service.StudyTraceQueryService;
import pers.juumii.service.StudyTraceService;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.TimeUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyTraceQueryServiceImpl implements StudyTraceQueryService {

    private final StudyTraceService studyTraceService;
    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;

    @Autowired
    public StudyTraceQueryServiceImpl(
            StudyTraceService studyTraceService,
            CoreClient coreClient,
            EnhancerClient enhancerClient) {
        this.studyTraceService = studyTraceService;
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
    }

    @Override
    public StudyTraceEnhancerInfo getStudyTraceEnhancerInfo(Long enhancerId) {
        StudyTraceEnhancerInfo res = new StudyTraceEnhancerInfo();
        res.setEnhancerId(enhancerId.toString());
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfEnhancer(enhancerId);
        if(traces.size() == 0) return res;
        res.setDuration(traces.stream()
                .map(StudyTrace::duration)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO)
                .getSeconds());
        res.setReview(traces.size());
        res.setMoments(traces.stream()
                .map(trace -> TimeUtils.format(trace.getStartTime()))
                .toList());
        res.setMomentsWithDuration(
                traces.stream().collect(
                Collectors.toMap(
                    trace->TimeUtils.format(trace.getStartTime()),
                    trace -> trace.duration().getSeconds())));
        return res;
    }

    @Override
    public List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId) {
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnode(knodeId);
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        HashMap<String, KnodeDTO> knodeIdMap = new HashMap<>();
        Map<String, Long> durationMap = new HashMap<>();
        Map<String, Integer> reviewMap = new HashMap<>();
        Map<String, List<String>> momentsMap = new HashMap<>();
        for(KnodeDTO offspring: offsprings){
            knodeIdMap.put(offspring.getId(), offspring);
            durationMap.put(offspring.getId(), 0L);
            reviewMap.put(offspring.getId(), 0);
            momentsMap.put(offspring.getId(), new ArrayList<>());
        }
        for(StudyTrace trace: traces){
            List<Long> knodeIds = studyTraceService.getTraceKnodeRels(trace.getId());
            int size = knodeIds.size();
            long duration = trace.duration().getSeconds();
            long portion = duration / size;
            for (Long _knodeId: knodeIds.stream().filter(id->durationMap.containsKey(id.toString())).toList())
                for(String ancestorId: knodeAncestorIds(_knodeId, knodeIdMap)){
                    durationMap.compute(ancestorId, (id, _duration)->_duration + portion);
                    reviewMap.compute(ancestorId, (id,review)->review+1);
                    momentsMap.compute(ancestorId, (id, moments)->
                        DataUtils.join(moments, TimeUtils.format(trace.getStartTime())));
                }
        }
        List<StudyTraceKnodeInfo> res = new ArrayList<>();
        for(KnodeDTO offspring: offsprings){
            StudyTraceKnodeInfo item = new StudyTraceKnodeInfo();
            String offspringId = offspring.getId();
            item.setKnodeId(offspringId);
            item.setDuration(durationMap.get(offspringId));
            item.setReview(reviewMap.get(offspringId));
            item.setMoments(momentsMap.get(offspringId));
            res.add(item);
        }
        return res;
    }

    @Override
    public List<StudyTraceEnhancerInfo> getStudyTraceEnhancerInfoUnderKnode(Long knodeId) {
        List<StudyTrace> traces = studyTraceService.getUserStudyTraces(null);
        List<Long> enhancerIds = traces.stream()
                .map(trace -> studyTraceService.getTraceEnhancerRels(trace.getId()))
                // List<List> -> List
                .flatMap(Collection::stream)
                // 去重
                .collect(Collectors.toSet())
                // 检查如果这个enhancer的关联knode是子knode，则选中这一enhancer
                .stream().filter((enhancerId)->{
                    List<KnodeDTO> knodes = enhancerClient.getKnodeByEnhancerId(enhancerId);
                    List<Long> knodeIds = knodes.stream().map(knode -> Convert.toLong(knode.getId())).toList();
                    return DataUtils.ifAny(knodeIds, _knodeId->coreClient.isOffspring(_knodeId, knodeId));
                }).toList();

        return enhancerIds.stream()
                .map(this::getStudyTraceEnhancerInfo)
                // 过滤掉没有学习记录的enhancer
                .filter(info -> info.getMoments() != null && info.getMoments().size() > 0)
                .toList();
    }

    private List<String> knodeAncestorIds(Long coverId, Map<String, KnodeDTO> knodeIdMap) {
        List<String> res = new ArrayList<>();
        KnodeDTO knode = knodeIdMap.get(coverId.toString());
        while (knode != null){
            res.add(knode.getId());
            knode = knodeIdMap.get(knode.getStemId());
        }
        return res;
    }
}
