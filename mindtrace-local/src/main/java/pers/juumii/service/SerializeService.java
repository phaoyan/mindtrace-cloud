package pers.juumii.service;

import org.springframework.http.ResponseEntity;

public interface SerializeService {
    ResponseEntity<byte[]> serializeAll(Long knodeId);

}
