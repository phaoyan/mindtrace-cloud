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
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.enhancer.EnhancerGroupDTO;
import pers.juumii.dto.enhancer.ResourceDTO;
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

        return wrapResult(offsprings.get(0).getTitle(), markdownBuilder.buildMarkdownInLayer(titles, contents, layerMap));
    }

    @Override
    public ResponseEntity<byte[]> serializeEnhancerContent(Long enhancerId) {
        EnhancerDTO enhancer = enhancerClient.getEnhancerById(enhancerId);
        return wrapResult(enhancer.getTitle(), getEnhancerMarkdown(enhancerId));
    }

    @Override
    public ResponseEntity<byte[]> serializeEnhancerContents(String title, List<Long> enhancerIds) {
        StringBuilder res = new StringBuilder();
        List<String> contents = enhancerIds.stream().map(this::getEnhancerMarkdown).toList();
        contents.forEach(content->res.append(content).append("\n"));
        return wrapResult(title, res.toString());
    }

    @Override
    public ResponseEntity<byte[]> serializeEnhancerGroupContent(Long groupId) {
        EnhancerGroupDTO group = enhancerClient.getEnhancerGroupById(groupId);
        List<Long> enhancerIds = enhancerClient.getRelatedEnhancerIdsByGroupId(groupId).stream().map(Convert::toLong).toList();
        return serializeEnhancerContents(group.getTitle(), enhancerIds);
    }

    @Override
    public String getEnhancerMarkdown(Long enhancerId) {
        StringBuilder res = new StringBuilder();
        EnhancerDTO enhancer = enhancerClient.getEnhancerById(enhancerId);
        List<ResourceDTO> resources = enhancerClient.getResourcesOfEnhancer(enhancerId);
        List<String> contents = resources.stream().map(markdownBuilder::buildResourceContent).toList();
        res.append("### ").append(enhancer.getTitle()).append("\n");
        contents.forEach(content->res.append(content).append("\n"));
        return res.toString();
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
                    .append(i == resources.size() - 1 ? "\n\n" : "\n");
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
            for(KnodeDTO br:
                    top.getBranchIds().stream()
                    .map(idMap::get)
                    .sorted(Comparator.comparingInt(knode->-knode.getIndex()))
                    .filter(br->!visited.contains(br))
                    .toList())
                stack.push(br);
        }

        return res;
    }

    private ResponseEntity<byte[]> wrapResult(String title, String content){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        title = new String(title.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        headers.setContentDispositionFormData("attachment", title + ".md");
        return ResponseEntity.ok()
                .headers(headers)
                .body(content.getBytes(StandardCharsets.UTF_8));
    }
}
