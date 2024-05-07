package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pers.juumii.feign.MqClient;
import pers.juumii.mq.MessageEvents;

import java.util.List;
import java.util.Objects;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final LoadBalancerClient loadBalancerClient;
    private final MqClient mqClient;
    private final StringRedisTemplate redis;

    @Autowired
    public MqEventRegistration(
            LoadBalancerClient loadBalancerClient,
            MqClient mqClient,
            StringRedisTemplate redis) {
        this.loadBalancerClient = loadBalancerClient;
        this.mqClient = mqClient;
        this.redis = redis;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-core");
        mqClient.addListener(
                MessageEvents.UPDATE_KNODE,
                MessageEvents.buildUrl(self, "/mq/knode/update"));
    }

    public void handleUpdateKnode(String data) {
        //更新 vector update list，每天04:00定时任务根据这个list更新 vector base 中的数据
        Long knodeId = Convert.toLong(data);
        String key = RedisKeys.VECTOR_UPDATE_LIST;
        String raw = redis.opsForValue().get(key);
        if(Objects.isNull(raw)) raw = "[]";
        List<Long> knodeIds = JSONUtil.toList(raw, Long.class);
        if(knodeIds.contains(knodeId)) return;
        knodeIds.add(knodeId);
        redis.opsForValue().set(key, JSONUtil.toJsonStr(knodeIds));
    }
}
