package org.hmgics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hmgics.model.MotorNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, MotorNotification> mNRedisTemplate(
            RedisConnectionFactory cf, ObjectMapper om) {
        RedisTemplate<String, MotorNotification> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);

        StringRedisSerializer stringSer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer(om);

        redisTemplate.setKeySerializer(stringSer);
        redisTemplate.setValueSerializer(jsonSer);
        redisTemplate.setHashKeySerializer(stringSer);
        redisTemplate.setHashValueSerializer(jsonSer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}