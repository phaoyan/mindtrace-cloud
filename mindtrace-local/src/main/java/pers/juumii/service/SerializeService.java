package pers.juumii.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SerializeService {
    ResponseEntity<byte[]> serializeContentsToMarkdown(Long knodeId);

    ResponseEntity<byte[]> serializeEnhancerContent(Long enhancerId);

    ResponseEntity<byte[]> serializeEnhancerContents(String title, List<Long> enhancerIds);

    ResponseEntity<byte[]> serializeEnhancerGroupContent(Long groupId);

    String getEnhancerMarkdown(Long enhancerId);

}
