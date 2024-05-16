package pers.juumii.service.impl.embedding;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Enhancer;
import pers.juumii.data.EnhancerResourceRel;
import pers.juumii.mapper.EnhancerResourceRelationshipMapper;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class LinkoutEmbeddingImpl implements ResourceEmbeddingService{
    private final ResourceService resourceService;
    private final EnhancerService enhancerService;
    private final EnhancerResourceRelationshipMapper errMapper;

    @Autowired
    public LinkoutEmbeddingImpl(
            ResourceService resourceService,
            EnhancerService enhancerService,
            EnhancerResourceRelationshipMapper errMapper) {
        this.resourceService = resourceService;
        this.enhancerService = enhancerService;
        this.errMapper = errMapper;
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
        String url = json.getStr("url");
        String remark = json.getStr("remark");
        return StringUtils.spacedTexts(enhancerTitles) + " " + remark + " " + url;
    }

    @Override
    public Boolean match(String resourceType) {
        return resourceType.equals(ResourceTypes.LINKOUT);
    }
}
