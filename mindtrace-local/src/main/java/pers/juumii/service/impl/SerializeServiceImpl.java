package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import pers.juumii.service.SerializeService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SerializeServiceImpl implements SerializeService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final TracingClient tracingClient;

    @Autowired
    public SerializeServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            TracingClient tracingClient) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.tracingClient = tracingClient;
    }

    @Override
    public ResponseEntity<byte[]> serializeAll(Long knodeId) {
        KnodeInfoCollection res = new KnodeInfoCollection();
        List<KnodeDTO> knodes = coreClient.offsprings(knodeId);
        List<Long> knodeIds = knodes.stream().map((knode) -> Convert.toLong(knode.getId())).toList();
        List<EnhancerDTO> enhancers = enhancerClient.getEnhancerOfKnodeBatch(knodeIds);
        List<Long> enhancerIds = enhancers.stream().map(enhancer -> Convert.toLong(enhancer.getId())).toList();
        List<ResourceDTO> resources = enhancerClient.getResourcesOfEnhancerBatch(enhancerIds);
        List<Long> resourceIds = resources.stream().map(resource -> Convert.toLong(resource.getId())).toList();
        List<StudyTraceDTO> traces = tracingClient.getStudyTracesOfKnodeBatch(knodeIds);
        List<Long> traceIds = traces.stream().map(trace -> Convert.toLong(trace.getId())).toList();
        List<IdPair> knodeEnhancerRels = enhancerClient.getKnodeEnhancerRels(knodeIds);
        List<IdPair> enhancerResourceRels = enhancerClient.getEnhancerResourceRels(enhancerIds);
        List<IdPair> traceKnodeRels = tracingClient.getTraceKnodeRels(traceIds);
        List<IdPair> traceEnhancerRels = tracingClient.getTraceEnhancerRels(traceIds);
        res.setKnodes(knodes);
        res.setEnhancers(enhancers);
        res.setResources(resources);
        res.setStudyTraces(traces);
        res.setKnodeEnhancerRels(knodeEnhancerRels);
        res.setEnhancerResourceRels(enhancerResourceRels);
        res.setTraceKnodeRels(traceKnodeRels);
        res.setTraceEnhancerRels(traceEnhancerRels);

        Map<String, Map<String, byte[]>> dataList = resourceIds.stream()
                .map(resourceId -> Map.entry(resourceId.toString(), enhancerClient.getDataFromResource(resourceId)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, byte[]> dataIndex = new HashMap<>();
        Map<String, Map<String, String>> dataIndexList = dataList.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (item) ->
                item.getValue().entrySet().stream().map((data) -> {
                    String id = Convert.toStr(IdUtil.getSnowflakeNextId());
                    dataIndex.put(id, data.getValue());
                    return Map.entry(data.getKey(), id);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        res.setDataList(dataIndexList);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(output);
        try {
            zipOutputStream.putNextEntry(new ZipEntry("main.json"));
            zipOutputStream.write(JSONUtil.toJsonStr(res).getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
            for (Map.Entry<String, byte[]> entry: dataIndex.entrySet()){
                zipOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
                zipOutputStream.write(entry.getValue(), 0, entry.getValue().length);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.zip");
        return ResponseEntity.ok()
                .headers(headers)
                .body(output.toByteArray());
    }
}
