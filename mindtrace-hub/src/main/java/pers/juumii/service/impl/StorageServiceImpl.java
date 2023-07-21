package pers.juumii.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pers.juumii.data.Metadata;
import pers.juumii.mapper.MetadataMapper;
import pers.juumii.service.StorageService;
import pers.juumii.utils.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {

    @NacosValue(value = "${tencent.cos.bucket.name}", autoRefreshed = true)
    private String BUCKET_NAME;

    private final MetadataMapper metadataMapper;
    private final COSClient cosClient;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public StorageServiceImpl(
            MetadataMapper metadataMapper,
            COSClient cosClient,
            DiscoveryClient discoveryClient) {
        this.metadataMapper = metadataMapper;
        this.cosClient = cosClient;
        this.discoveryClient = discoveryClient;
    }



    @Override
    public Metadata push(Long userId, String title, InputStream data, String contentType) {
        long resourceId = IdUtil.getSnowflakeNextId();
        String objKey = userId + "/" + resourceId + "/" + title;
        cosClient.putObject(BUCKET_NAME, objKey, data, new ObjectMetadata());
        Optional<ServiceInstance> gateway = discoveryClient.getInstances("mindtrace-gateway").stream().findAny();
        if(gateway.isEmpty())
            throw new RuntimeException("Service Not Available: mindtrace-gateway");
        String url = gateway.get().getUri().toString() + "/hub/resource/" + resourceId;
        Metadata metadata = Metadata.prototype(userId, title, url, contentType);
        metadata.setId(resourceId);
        metadataMapper.insert(metadata);
        return metadata;
    }

    @Override
    public ResponseEntity<byte[]> pull(Long resourceId) {
        try{
            Metadata metadata = metadataMapper.selectById(resourceId);
            String objKey = metadata.getUserId() + "/" + metadata.getId() + "/" + metadata.getTitle();
            byte[] bytes = cosClient.getObject(BUCKET_NAME, objKey).getObjectContent().readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(metadata.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + metadata.getTitle() + "\"")
                    // 显式地将文件的标题写在请求头上方便解析
                    .header(HttpUtils.MINDTRACE_RESOURCE_TITLE, metadata.getTitle())
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=7200")
                    .body(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public void remove(Long resourceId) {
        // todo
    }

    @Override
    public Boolean exists(Long resourceId) {
        Metadata metadata = metadataMapper.selectById(resourceId);
        if(metadata == null) return false;
        String objKey = metadata.getUserId() + "/" + metadata.getId() + "/" + metadata.getTitle();
        return cosClient.doesObjectExist(BUCKET_NAME, objKey);
    }

    @Override
    public void setMeta(Long userId, Long resourceId, Map<String, Object> meta) {
        String objKey = userId + "/" + resourceId;
        ObjectMetadata metadata = cosClient.getObjectMetadata(BUCKET_NAME, objKey);
        metadata.setHeader("x-cos-metadata-directive", "Replaced");
        for(Map.Entry<String, Object> header: meta.entrySet())
            metadata.setHeader(header.getKey(), header.getValue());
        CopyObjectRequest request = new CopyObjectRequest(BUCKET_NAME, objKey, BUCKET_NAME, objKey);
        request.setNewObjectMetadata(metadata);
        cosClient.copyObject(request);
    }

    @Override
    public Map<String, Object> getMeta(Long userId, Long resourceId) {
        String objKey = userId + "/" + resourceId;
        return cosClient.getObjectMetadata(BUCKET_NAME, objKey).getRawMetadata();
    }

    @Override
    public List<Metadata> getMetadataList(Long userId) {
        LambdaQueryWrapper<Metadata> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Metadata::getUserId, userId);
        return metadataMapper.selectList(wrapper);
    }

}
