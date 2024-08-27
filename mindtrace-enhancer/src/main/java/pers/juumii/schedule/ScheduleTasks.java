package pers.juumii.schedule;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.config.RedisKeys;
import pers.juumii.feign.VectorClient;
import pers.juumii.service.ResourceService;

import java.util.List;

@RestController
public class ScheduleTasks {

    private final VectorClient vectorClient;
    private final ResourceService resourceService;
    private final StringRedisTemplate redis;

    @Autowired
    public ScheduleTasks(VectorClient vectorClient, ResourceService resourceService, StringRedisTemplate redis) {
        this.vectorClient = vectorClient;
        this.resourceService = resourceService;
        this.redis = redis;
    }


    @Scheduled(cron = "0 0 4 * * ?")
    @PostMapping("/schedule/vector")
    public void updateResourceVectorBase() {
        String key = RedisKeys.RESOURCE_VECTOR_UPDATE_LIST;
        String raw = redis.opsForValue().get(key);
        List<Long> resourceIds = JSONUtil.toList(raw, Long.class);
        resourceIds.stream()
                .filter(resourceId->resourceService.getResource(resourceId) != null)
                .map(resourceId->JSONUtil.createObj()
                        .set("txt", resourceService.getEmbeddingText(resourceId))
                        .set("id", resourceId)
                        .set("vector-base", "resource"))
                .forEach(json->vectorClient.addVector(JSONUtil.toJsonStr(json)));
        redis.opsForValue().set(key, "[]");
    }

}
