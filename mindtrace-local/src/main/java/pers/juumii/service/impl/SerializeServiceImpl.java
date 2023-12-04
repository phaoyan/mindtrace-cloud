package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
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
import pers.juumii.service.impl.utils.MarkdownBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SerializeServiceImpl implements SerializeService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final TracingClient tracingClient;
    private final MarkdownBuilder markdownBuilder;

    @Autowired
    public SerializeServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            TracingClient tracingClient,
            MarkdownBuilder markdownBuilder) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.tracingClient = tracingClient;
        this.markdownBuilder = markdownBuilder;
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

        // 此句性能不佳
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

    @Override
    public ResponseEntity<byte[]> serializeContentsToMarkdown(Long knodeId) {
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        // 将拿到的knode按照深度优先搜索的方式排序，这样Markdown构建只需要使用for循环就可以有序地实现
        // 返回的Map是Knode Index -> 层数，方便确定该knode的层级以确定标题的级数
        List<KnodeDTO> sorted = treeSort(offsprings);
        List<Long> layerMap = getLayerMap(sorted);
        List<String> titles = sorted.stream().map(KnodeDTO::getTitle).toList();

        //使用多线程提高响应速度
        AtomicInteger countStart = new AtomicInteger(0);
        AtomicInteger countEnd = new AtomicInteger(0);
        List<String> contents = new ArrayList<>(sorted.size());

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                Math.min(128, Math.max(32, offsprings.size() / 8)), 128,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        for(int i = 0; i < sorted.size(); i ++){
            contents.add(null);
            int finalI = i;
            threadPool.execute(()->{
                countStart.incrementAndGet();
                String content = getKnodeContent(Convert.toLong(sorted.get(finalI).getId()));
                contents.set(finalI, content);
                countEnd.incrementAndGet();
            });
        }
        long start = System.currentTimeMillis();
        long threshold = 300000L; //超过5min视为出现问题
        while (countEnd.get() < sorted.size() && System.currentTimeMillis() - start < threshold){}
        if(System.currentTimeMillis() - start >= threshold)
            throw new RuntimeException("请求超时");
        threadPool.shutdown();

        String markdown = markdownBuilder.buildMarkdownInLayer(titles, contents, layerMap);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.md");
        return ResponseEntity.ok()
                .headers(headers)
                .body(markdown.getBytes(StandardCharsets.UTF_8));
    }

    private List<Long> getLayerMap(List<KnodeDTO> sorted) {
        List<Long> res = new ArrayList<>();
        HashMap<String, KnodeDTO> idMap = new HashMap<>();
        for(KnodeDTO knode: sorted)
            idMap.put(knode.getId(), knode);
        for(KnodeDTO knode: sorted){
            Long layer = 0L;
            while (knode != null){
                knode = idMap.get(knode.getStemId());
                layer ++;
            }
            res.add(layer);
        }
        return res;
    }

    private String getKnodeContent(Long knodeId) {
        List<EnhancerDTO> enhancers = enhancerClient.getEnhancersOfKnode(knodeId);
        if(enhancers.isEmpty()) return "";
        StringBuilder res = new StringBuilder();
        for(EnhancerDTO enhancer: enhancers){
            res.append("*").append(enhancer.getTitle()).append("*").append("\n"); // Enhancer标题
            List<ResourceDTO> resources = enhancerClient.getResourcesOfEnhancer(Convert.toLong(enhancer.getId()));
            for(int i = 0; i < resources.size(); i ++)
                res.append(markdownBuilder.buildResourceContent(resources.get(i)))
                    .append(i == resources.size() - 1 ? "\n" : "\n --- \n");
        }
        return res.toString();
    }

    private List<KnodeDTO> treeSort(List<KnodeDTO> offsprings) {
        List<KnodeDTO> res = new ArrayList<>();
        KnodeDTO root = offsprings.get(0);
        Stack<KnodeDTO> stack = new Stack<>();
        HashSet<KnodeDTO> visited = new HashSet<>();
        HashMap<String, KnodeDTO> idMap = new HashMap<>();
        for(KnodeDTO knode: offsprings)
            idMap.put(knode.getId(), knode);

        stack.push(root);
        while (!stack.isEmpty()){
            KnodeDTO top = stack.pop();
            visited.add(top);
            res.add(top);
            for(String knodeId: top.getBranchIds()){
                KnodeDTO br = idMap.get(knodeId);
                if(!visited.contains(br))
                    stack.push(br);
            }
        }

        return res;
    }
}
