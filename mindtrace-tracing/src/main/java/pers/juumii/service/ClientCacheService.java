package pers.juumii.service;

public interface ClientCacheService {

    void updateCache(Long userId, String key, String data);

    String getCache(Long userId, String key);

    void clearCache(Long userId, String key);

}
