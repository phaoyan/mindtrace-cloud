package pers.juumii.service;

import java.util.Map;

public interface ClientCacheService {

    void updateCache(Long userId, Map<String, Object> cache);

    Map<String, Object> getCache(Long userId);

    void clearCache(Long userId);

}
