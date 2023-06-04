package pers.juumii.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory){
        StringRedisTemplate res = new StringRedisTemplate(factory);
        res.setKeySerializer(new StringRedisSerializer());
        res.setValueSerializer(new StringRedisSerializer());
        return res;
    }
}
