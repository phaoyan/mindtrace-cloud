package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.StudyTraceQueryService;
import pers.juumii.service.StudyTraceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudyTraceQueryServiceImpl implements StudyTraceQueryService {

    private final StudyTraceService studyTraceService;
    private final CoreClient coreClient;

    @Autowired
    public StudyTraceQueryServiceImpl(
            StudyTraceService studyTraceService,
            CoreClient coreClient) {
        this.studyTraceService = studyTraceService;
        this.coreClient = coreClient;
    }

    @Override
    public Map<String, Long> getStudyTimeDistribution(Long knodeId) {
        List<StudyTrace> traces = studyTraceService.getStudyTracesOfKnode(knodeId);
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        HashMap<String, KnodeDTO> knodeIdMap = new HashMap<>();
        Map<String, Long> map = new HashMap<>();
        for(KnodeDTO offspring: offsprings){
            knodeIdMap.put(offspring.getId(), offspring);
            map.put(offspring.getId(), 0L);
        }
        for(StudyTrace trace: traces){
            List<Long> coverages = studyTraceService.getTraceCoverages(trace.getId());
            int size = coverages.size();
            long duration = trace.duration().getSeconds();
            long portion = duration / size;
            for (Long coverId: coverages)
                if(map.containsKey(coverId.toString()))
                    for(String ancestorId: knodeAncestorIds(coverId, knodeIdMap))
                        map.put(ancestorId, map.get(ancestorId) + portion);
        }
        return map;
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
