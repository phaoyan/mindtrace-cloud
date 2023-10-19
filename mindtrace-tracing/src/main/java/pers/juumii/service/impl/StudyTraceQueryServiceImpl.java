package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.tracing.EnhancerStudyTimeline;
import pers.juumii.dto.tracing.EnhancerStudyTimelineItem;
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
        List<StudyTrace> traces = studyTraceService
                .getStudyTracesOfEnhancer(enhancerId)
                .stream().filter(Objects::nonNull)
                .toList();
        if(traces.size() == 0) return res;
        res.setDuration(traces.stream()
                .map(StudyTrace::duration)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO)
                .getSeconds());
        res.setReview(traces.size());
        res.setTraces(StudyTrace.transfer(traces));
        return res;
    }

    @Override
    public List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId) {
        // 为了只调用一次offspring提速，将getStudyTraceOfKnode的逻辑在此重写一遍
        List<Long> offspringIds = coreClient.offspringIds(knodeId);
        HashSet<Long> offspringIdSet = new HashSet<>(offspringIds);
        KnodeDTO knode = coreClient.check(knodeId);
        List<StudyTrace> allTraces = studyTraceService.getUserStudyTraces(Convert.toLong(knode.getCreateBy()));
        List<StudyTrace> traces = new ArrayList<>();
        for(StudyTrace trace: allTraces){
            for (Long kid: studyTraceService.getTraceKnodeRels(trace.getId()))
                if(offspringIdSet.contains(kid)){
                    traces.add(trace);
                    break;
                }
        }
        Map<Long, Long> durationMap = new HashMap<>();
        Map<Long, Integer> reviewMap = new HashMap<>();
        Map<Long, List<String>> momentsMap = new HashMap<>();
        for(Long offspringId: offspringIds){
            durationMap.put(offspringId, 0L);
            reviewMap.put(offspringId, 0);
            momentsMap.put(offspringId, new ArrayList<>());
        }
        Map<Long, List<Long>> ancestorSeriesList = coreClient.ancestorIdsBatch(offspringIds);
        for(StudyTrace trace: traces){
            List<Long> knodeIds = studyTraceService.getTraceKnodeRels(trace.getId());
            int size = knodeIds.size();
            long duration = trace.duration().getSeconds();
            long portion = duration / size;
            for (Long _knodeId: knodeIds.stream().filter(durationMap::containsKey).toList())
                for(Long ancestorId: ancestorSeriesList.get(_knodeId)){
                    if(!durationMap.containsKey(ancestorId)) continue;
                    durationMap.compute(ancestorId, (id, _duration)->_duration + portion);
                    reviewMap.compute(ancestorId, (id,review)->review + 1);
                    momentsMap.compute(ancestorId, (id, moments)->
                        DataUtils.join(moments, TimeUtils.format(trace.getStartTime())));
                }
        }
        List<StudyTraceKnodeInfo> res = new ArrayList<>();
        for(Long offspringId: offspringIds){
            StudyTraceKnodeInfo item = new StudyTraceKnodeInfo();
            item.setKnodeId(offspringId.toString());
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
                .filter(info -> info.getTraces() != null && !info.getTraces().isEmpty())
                .toList();
    }

    @Override
    public EnhancerStudyTimeline getEnhancerStudyTimeline(Long knodeId, Long minDuration, Long minInterval) {
        EnhancerStudyTimeline res = new EnhancerStudyTimeline();
        res.setKnodeId(knodeId.toString());
        res.setMinInterval(minInterval);
        res.setMinDuration(minDuration);
        List<EnhancerStudyTimelineItem> items = new ArrayList<>();
        res.setItems(items);
        List<Long> traceIds =
                studyTraceService.getStudyTracesOfKnode(knodeId)
                .stream().map(StudyTrace::getId)
                .toList();
        List<IdPair> rels = studyTraceService.getTraceEnhancerRels(traceIds);
        Map<String, List<IdPair>> relMap = rels.stream().collect(Collectors.groupingBy(IdPair::getRightId));
        for(String enhancerId: relMap.keySet()){
            List<StudyTrace> traces = relMap.get(enhancerId).stream()
                    .map(idPair -> studyTraceService.getStudyTrace(Convert.toLong(idPair.getLeftId())))
                    .sorted(Comparator.comparing(StudyTrace::getStartTime))
                    .toList();
            List<List<StudyTrace>> splices = new ArrayList<>();
            for(StudyTrace trace: traces){
                if(splices.isEmpty()){
                    splices.add(new ArrayList<>(List.of(trace)));
                    continue;
                }
                List<StudyTrace> lastSplice = splices.get(splices.size() - 1);
                StudyTrace lastTrace = lastSplice.get(lastSplice.size() - 1);
                if(Duration.between(lastTrace.getStartTime(), trace.getStartTime()).toSeconds() > minInterval)
                    splices.add(new ArrayList<>(List.of(trace)));
                else lastSplice.add(trace);
            }
            for(List<StudyTrace> splice: splices){
                Long duration = splice.stream().map(trace -> trace.duration().getSeconds()).reduce(Long::sum).orElseGet(() -> 0L);
                if(duration < minDuration) continue;
                String start = TimeUtils.format(splice.get(0).getStartTime());
                String end = TimeUtils.format(splice.get(splice.size() - 1).getEndTime());
                int period = splice.size();
                EnhancerStudyTimelineItem item = new EnhancerStudyTimelineItem();
                item.setEnhancerId(enhancerId);
                item.setEnhancer(enhancerClient.getEnhancerById(Convert.toLong(enhancerId)));
                item.setStart(start);
                item.setEnd(end);
                item.setDuration(duration);
                item.setPeriods(period);
                item.setTraceIds(splice.stream().map(tr->tr.getId().toString()).toList());
                items.add(item);
            }
        }
        res.getItems().sort(Comparator.comparing(item->TimeUtils.parse(item.getStart())));
        return res;
    }


}
