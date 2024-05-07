package pers.juumii.service.impl.embedding;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.service.ResourceService;

import java.nio.charset.StandardCharsets;

@Service
public class HubResourceEmbeddingImpl implements ResourceEmbeddingService{
    private final ResourceService resourceService;

    @Autowired
    public HubResourceEmbeddingImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public String getEmbeddingText(Long resourceId) {
        byte[] data = resourceService.getDataFromResource(resourceId, "data.json");
        String raw = new String(data, StandardCharsets.UTF_8);
        JSONObject json = JSONUtil.parseObj(raw);
        return json.getStr("remark");
    }

    @Override
    public Boolean match(String resourceType) {
        return resourceType.equals(ResourceTypes.MINDTRACE_HUB_RESOURCE);
    }
}
