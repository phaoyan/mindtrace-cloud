package pers.juumii.service;

import org.springframework.http.ResponseEntity;
import pers.juumii.data.Metadata;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface StorageService {

    List<Metadata> getMetadataList(Long userId);

    Metadata push(Long userId, String title, InputStream data, String contentType);

    ResponseEntity<byte[]> pull(Long resourceId);

    void remove(Long resourceId);

    Boolean exists(Long resourceId);

    void setMeta(Long userId, Long resourceId, Map<String, Object> meta);

    Map<String, Object> getMeta(Long userId, Long resourceId);
}
