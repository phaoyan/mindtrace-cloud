package pers.juumii.service.impl.embedding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.service.ResourceService;

import java.nio.charset.StandardCharsets;

@Service
public class ClozeEmbeddingImpl implements ResourceEmbeddingService{
    private final ResourceService resourceService;

    @Autowired
    public ClozeEmbeddingImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public String getEmbeddingText(Long resourceId) {
        byte[] data = resourceService.getDataFromResource(resourceId, "raw.md");
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public Boolean match(String resourceType) {
        return resourceType.equals(ResourceTypes.CLOZE);
    }
}
