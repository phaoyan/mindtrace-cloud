package pers.juumii.service.impl.embedding;

import org.springframework.stereotype.Service;

@Service
public interface ResourceEmbeddingService {
    String getEmbeddingText(Long resourceId);
    Boolean match(String resourceType);
}
