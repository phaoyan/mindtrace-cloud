package pers.juumii.schedule;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.config.RedisKeys;
import pers.juumii.feign.VectorClient;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.utils.StringUtils;

import java.util.List;

@RestController
public class ScheduleTasks {

    private final VectorClient vectorClient;
    private final KnodeQueryService knodeQuery;
    private final StringRedisTemplate redis;

    public ScheduleTasks(VectorClient vectorClient, KnodeQueryService knodeQuery, StringRedisTemplate redis) {
        this.vectorClient = vectorClient;
        this.knodeQuery = knodeQuery;
        this.redis = redis;
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @PostMapping("/schedule/vector")
    public void updateKnodeVectorBase() {
        String key = RedisKeys.VECTOR_UPDATE_LIST;
        String raw = redis.opsForValue().get(key);
        List<Long> knodeIds = JSONUtil.toList(raw, Long.class);
        knodeIds.stream()
                .filter(knodeId->knodeQuery.check(knodeId) != null)
                .map(knodeId->JSONUtil.createObj()
                        .set("txt", StringUtils.spacedTexts(knodeQuery.chainStyleTitle(knodeId)))
                        .set("id", knodeId)
                        .set("vector-base", "knode"))
                .forEach(json->vectorClient.addVector(JSONUtil.toJsonStr(json)));
        redis.opsForValue().set(key, "[]");
    }
}
