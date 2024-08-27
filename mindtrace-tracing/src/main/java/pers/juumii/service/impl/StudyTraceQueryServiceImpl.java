package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.enhancer.EnhancerGroupDTO;
import pers.juumii.dto.tracing.*;
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
        res.setTraceIds(new ArrayList<>());
        EnhancerDTO enhancer = enhancerClient.getEnhancerById(enhancerId);
        List<StudyTrace> traces = studyTraceService
                .getStudyTracesOfEnhancer(enhancerId)
                .stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(StudyTrace::getStartTime))
                .toList();
        if(traces.size() == 0) return res;
        res.setEnhancerId(enhancerId.toString());
        res.setTitle(enhancer.getTitle());
        res.setDuration(traces.stream()
                .map(StudyTrace::getSeconds)
                .reduce(Long::sum)
                .orElse(0L));
        res.setReview(traces.size());
        res.setTraceIds(DataUtils.reverse(traces.stream().map(trace->Convert.toStr(trace.getId())).toList()));
        return res;
    }

    @Override
    public StudyTraceEnhancerGroupInfo getStudyTraceEnhancerGroupInfo(Long groupId) {
        EnhancerGroupDTO group = enhancerClient.getEnhancerGroupById(groupId);
        List<Long> enhancerIds = enhancerClient
                .getRelatedEnhancerIdsByGroupId(groupId)
                .stream().map(Convert::toLong).toList();
        List<StudyTraceEnhancerInfo> infos = enhancerIds.stream().map(this::getStudyTraceEnhancerInfo).toList();
        if(infos.isEmpty()) return new StudyTraceEnhancerGroupInfo(
                Convert.toStr(groupId),
                group.getTitle(),
                0L, 0,
                new ArrayList<>());
        StudyTraceEnhancerGroupInfo res = new StudyTraceEnhancerGroupInfo();
        List<String> traceIds = infos.stream()
                .filter(info->Objects.nonNull(info.getEnhancerId()))
                .map(StudyTraceEnhancerInfo::getTraceIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).stream()
                .toList();
        res.setGroupId(Convert.toStr(groupId));
        res.setTitle(group.getTitle());
        res.setDuration(traceIds.stream()
                .map((traceId)->studyTraceService.getStudyTrace(Convert.toLong(traceId)).getSeconds())
                .reduce(Long::sum)
                .orElse(0L));
        res.setReview(traceIds.size());
        res.setTraceIds(traceIds);
        return res;
    }

    @Override
    public List<StudyTraceKnodeInfo> getStudyTraceKnodeInfo(Long knodeId) {
        List<Long> offspringIds = coreClient.offspringIds(knodeId);
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId);
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
            Set<Long> knodeIds = studyTraceService.getKnodeIdsByTraceId(trace.getId()).stream()
                    .filter(ancestorSeriesList::containsKey)
                    .collect(Collectors.toSet());
            Set<Long> relatedKnodeIds = knodeIds.stream()
                    .map(ancestorSeriesList::get)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            for(Long relatedKnodeId: relatedKnodeIds){
                durationMap.computeIfPresent(relatedKnodeId, (_id, duration) -> duration + trace.getSeconds());
                reviewMap.computeIfPresent(relatedKnodeId, (_id, review) -> review + 1);
                momentsMap.computeIfPresent(relatedKnodeId, (_id, moments) ->
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
        List<Long> enhancerIds = enhancerClient.getEnhancerIdsFromKnodeIncludingBeneath(knodeId);
        List<Long> tracedEnhancerIds = studyTraceService.getTracedEnhancerIdsFromList(enhancerIds);
        return tracedEnhancerIds.stream()
                .map(this::getStudyTraceEnhancerInfo)
                .filter(info -> info.getTraceIds() != null && !info.getTraceIds().isEmpty())
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
                studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId)
                .stream().map(StudyTrace::getId)
                .toList();
        List<IdPair> rels = studyTraceService.getEnhancerIdsByTraceId(traceIds);
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
                Long duration = splice.stream().map(StudyTrace::getSeconds).reduce(Long::sum).orElse(0L);
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

    @Override
    public Long currentMonthStudyTime(Long knodeId) {
        return studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId).stream()
                //过滤出当月的traces
                .filter(trace -> TimeUtils.inCurrentMonth(trace.getStartTime()))
                //按秒求和
                .mapToLong(StudyTrace::getSeconds).sum();
    }

    @Override
    public Map<String, Long> studyTimeAccumulation(Long knodeId) {
        HashMap<String, Long> res = new HashMap<>();
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId).stream()
                .sorted(Comparator.comparing(StudyTrace::getStartTime))
                .toList();
        long curDuration = 0L;
        for(StudyTrace tr: traces){
            curDuration += tr.getSeconds();
            res.put(tr.getId().toString(), curDuration);
        }
        return res;
    }

    @Override
    public Integer traceCount(Long knodeId) {
        return studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId).size();
    }

    @Override
    public Map<String, Long> calendarDay(Long knodeId) {
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId);
        Map<String, List<StudyTrace>> groups = traces.stream().collect(Collectors.groupingBy(tr->TimeUtils.format(tr.getStartTime()).substring(0,10)));
        HashMap<String, Long> res = new HashMap<>();
        for(String key: groups.keySet())
            res.put(key, groups.get(key).stream().mapToLong(StudyTrace::getSeconds).sum());
        return res;
    }

    @Override
    public Map<String, Long> calendarMonth(Long knodeId) {
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnodeIncludingBeneath(knodeId);
        Map<String, List<StudyTrace>> groups = traces.stream().collect(Collectors.groupingBy(tr->TimeUtils.format(tr.getStartTime()).substring(0,7)));
        HashMap<String, Long> res = new HashMap<>();
        for(String key: groups.keySet())
            res.put(key, groups.get(key).stream().mapToLong(StudyTrace::getSeconds).sum());
        return res;
    }


}
