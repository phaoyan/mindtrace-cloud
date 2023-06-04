package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import pers.juumii.service.ClientCacheService;

@Service
public class ClientCacheServiceImpl implements ClientCacheService {

    private final StringRedisTemplate redis;

    @Autowired
    public ClientCacheServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.redis = stringRedisTemplate;
    }

    @Override
    public void updateCache(Long userId, String key, String data) {
        if(userId == -1L) return;
        redis.opsForValue().set("mindtrace::user::" + userId + "::" + key, data);
    }

    @Override
    public String getCache(Long userId, String key) {
        ValueOperations<String, String> ops = redis.opsForValue();
        return ops.get("mindtrace::user::" + userId + "::" + key);
    }

    @Override
    public void clearCache(Long userId, String key) {
        redis.opsForValue().getAndDelete("mindtrace::user::" + userId + "::" + key);
    }
}
