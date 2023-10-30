package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.KnodeInfoCollection;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.feign.TracingClient;
import pers.juumii.service.ResolveService;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResolveServiceImpl implements ResolveService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final TracingClient tracingClient;

    @Autowired
    public ResolveServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            TracingClient tracingClient) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.tracingClient = tracingClient;
    }

    @Override
    public void resolve(Long stemId, KnodeInfoCollection main, Map<String, byte[]> dataIndex) {
        try {
            resolveKnodes(stemId, main);
            resolveEnhancers(main);
            resolveResources(main);
            resolveData(main, dataIndex);
            resolveTraces(main);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resolveTraces(KnodeInfoCollection data) {
        List<StudyTraceDTO> traces = data.getStudyTraces();
        List<IdPair> traceKnodeRels = data.getTraceKnodeRels();
        List<IdPair> traceEnhancerRels = data.getTraceEnhancerRels();
        for(StudyTraceDTO trace: traces){
            String oriTraceId = trace.getId();
            trace.setId(null);
            StudyTraceDTO newTrace = tracingClient.addStudyTrace(trace);
            DataUtils.forAllIf(
                    traceKnodeRels,
                    rel->rel.getLeftId().equals(oriTraceId),
                    rel->rel.setLeftId(newTrace.getId()));
            DataUtils.forAllIf(
                    traceEnhancerRels,
                    rel->rel.getLeftId().equals(oriTraceId),
                    rel->rel.setLeftId(newTrace.getId()));
        }
        for(IdPair traceKnodeRel: traceKnodeRels)
            tracingClient.addStudyTraceKnodeRel(traceKnodeRel);
        for(IdPair traceEnhancerRel: traceEnhancerRels)
            tracingClient.addStudyTraceEnhancerRel(traceEnhancerRel);
    }

    private void resolveData(KnodeInfoCollection main, Map<String, byte[]> dataIndex) {
        Map<String, Map<String, byte[]>> dataList = main.getDataList().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (item) -> item.getValue().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (data) -> dataIndex.get(data.getValue())))));
        for(String resourceId: dataList.keySet())
            enhancerClient.addDataToResource(Convert.toLong(resourceId), dataList.get(resourceId));
    }

    private void resolveResources(KnodeInfoCollection data) {
        List<ResourceDTO> resources = data.getResources();
        List<IdPair> rels = data.getEnhancerResourceRels();
        Map<String, List<String>> resourceToEnhancerRelMap = new HashMap<>();
        Map<String, Map<String, String>> dataList = data.getDataList();
        Map<String, Map<String, String>> newDataList = new HashMap<>();
        for(ResourceDTO resource: resources)
            resourceToEnhancerRelMap.put(resource.getId(), new ArrayList<>());
        for(IdPair rel: rels)
            resourceToEnhancerRelMap.compute(rel.getRightId(), (k,v)->{v.add(rel.getLeftId());return v;});
        for(ResourceDTO resource: resources){
            ResourceDTO added  = enhancerClient.addResource(null);
            Long addedId = Convert.toLong(added.getId());
            resourceToEnhancerRelMap.get(resource.getId()).forEach(enhancerId->
                    enhancerClient.addEnhancerResourceRel(Convert.toLong(enhancerId), addedId));
            enhancerClient.resourceEditTitle(addedId, resource.getTitle());
            enhancerClient.resourceEditType(addedId, resource.getType());
            enhancerClient.resourceEditCreateTime(addedId, resource.getCreateTime());
            //将id换成新的便于rel对接
            newDataList.put(added.getId(), dataList.get(resource.getId()));
            resource.setId(added.getId());
        }
        data.setDataList(newDataList);
    }

    private void resolveEnhancers(KnodeInfoCollection data) {
        List<EnhancerDTO> enhancers = data.getEnhancers();
        List<IdPair> knodeEnhancerRels = data.getKnodeEnhancerRels();
        Map<String, List<String>> enhancerToKnodeRelMap = new HashMap<>();
        for(EnhancerDTO enhancer: enhancers)
            enhancerToKnodeRelMap.put(enhancer.getId(), new ArrayList<>());
        for(IdPair rel: knodeEnhancerRels)
            enhancerToKnodeRelMap.compute(rel.getRightId(), (k,v)->{v.add(rel.getLeftId());return v;});
        for(EnhancerDTO enhancer: enhancers){
            EnhancerDTO added = enhancerClient.addEnhancer();
            enhancerToKnodeRelMap.get(enhancer.getId()).forEach(knodeId->
                enhancerClient.addKnodeEnhancerRel(Convert.toLong(knodeId), Convert.toLong(added.getId())));
            EnhancerDTO enhancerDTO = new EnhancerDTO();
            enhancerDTO.setTitle(enhancer.getTitle());
            enhancerDTO.setCreateTime(enhancer.getCreateTime());
            enhancerDTO.setIntroduction(enhancer.getIntroduction());
            enhancerDTO.setIsQuiz(enhancer.getIsQuiz());
            enhancerClient.updateEnhancer(Convert.toLong(added.getId()), enhancerDTO);
            //将id换成新的以便后续rel对接
            DataUtils.forAllIf(
                    data.getTraceEnhancerRels(),
                    rel->rel.getRightId().equals(enhancer.getId()),
                    rel->rel.setRightId(added.getId()));
            DataUtils.forAllIf(
                    data.getEnhancerResourceRels(),
                    rel->rel.getLeftId().equals(enhancer.getId()),
                    rel->rel.setLeftId(added.getId()));
            enhancer.setId(added.getId());
        }
    }

    private void resolveKnodes(Long stemId, KnodeInfoCollection data) {
        List<String> cacheKnodeIds = new ArrayList<>();
        for(KnodeDTO knode: data.getKnodes())
            resolveKnode(stemId, data, knode, cacheKnodeIds);
    }

    private Long resolveKnode(
            Long rootId,
            KnodeInfoCollection data,
            KnodeDTO knode,
            List<String> cacheKnodeIds) {
        if(knode == null) return rootId;
        else if(cacheKnodeIds.contains(knode.getId())) return Convert.toLong(knode.getId());
        Long stemId = resolveKnode(
                rootId, data,
                DataUtils.getIf(data.getKnodes(), st -> st.getId().equals(knode.getStemId())),
                cacheKnodeIds);
        KnodeDTO branch = coreClient.branch(stemId, knode.getTitle());
        coreClient.editCreateTime(Convert.toLong(branch.getId()), knode.getCreateTime());
        coreClient.editIndex(Convert.toLong(branch.getId()), knode.getIndex());
        // 将id切换为新的便于后续rel对接
        DataUtils.forAllIf(
                data.getTraceKnodeRels(),
                rel->rel.getRightId().equals(knode.getId()),
                (rel)->rel.setRightId(branch.getId()));
        DataUtils.forAllIf(
                data.getKnodeEnhancerRels(),
                rel->rel.getLeftId().equals(knode.getId()),
                (rel->rel.setLeftId(branch.getId())));
        DataUtils.forAllIf(
                data.getKnodes(),
                _knode->_knode.getStemId().equals(knode.getId()),
                _knode->_knode.setStemId(branch.getId()));
        knode.setId(branch.getId());
        cacheKnodeIds.add(branch.getId());
        return Convert.toLong(branch.getId());
    }
}
