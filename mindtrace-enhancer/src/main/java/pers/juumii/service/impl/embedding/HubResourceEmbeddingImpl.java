package pers.juumii.service.impl.embedding;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Enhancer;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class HubResourceEmbeddingImpl implements ResourceEmbeddingService{
    private final ResourceService resourceService;
    private final EnhancerService enhancerService;

    @Autowired
    public HubResourceEmbeddingImpl(ResourceService resourceService, EnhancerService enhancerService) {
        this.resourceService = resourceService;
        this.enhancerService = enhancerService;
    }

    @Override
    public String getEmbeddingText(Long resourceId) {
        byte[] data = resourceService.getDataFromResource(resourceId, "data.json");
        List<String> enhancerTitles = enhancerService.getEnhancersByResourceId(resourceId)
                .stream()
                .map(Enhancer::getTitle)
                .filter(title->!StrUtil.isBlank(title))
                .toList();
        String raw = new String(data, StandardCharsets.UTF_8);
        JSONObject json = JSONUtil.parseObj(raw);
        return json.getStr("remark") + json.getStr("description") + StringUtils.spacedTexts(enhancerTitles);
    }

    @Override
    public Boolean match(String resourceType) {
        return resourceType.equals(ResourceTypes.MINDTRACE_HUB_RESOURCE);
    }
}
