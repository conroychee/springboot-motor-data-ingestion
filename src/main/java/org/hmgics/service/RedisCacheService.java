package org.hmgics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hmgics.model.MotorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RedisCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    private final Jackson2HashMapper mapper = new Jackson2HashMapper(false); // no flatten
    private final RedisTemplate<String, MotorNotification> redisTemplate;
    private final ObjectMapper om;

    /**
     * Find the key that has a certain pattern from Redis
     * @param keyPattern
     * @return Matched Key
     */
    public Optional<String> findMatchedKey(String keyPattern) {

        Optional<String> matchedKey = Optional.empty();
        ScanOptions options = ScanOptions.scanOptions().match(keyPattern).build(); // build the scan options
        try (Cursor<String> cursor = redisTemplate.scan(options)) { // scan the pattern
            while (cursor.hasNext()) {
                matchedKey = Optional.of(cursor.next());
            }
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
        return matchedKey;
    }

    /**
     * Delete a key from redis
     * @param key
     */
    public void deleteMatchedKey(String key) {
        redisTemplate.delete(key);
    }

    public void upsert(MotorNotification m) {
        logger.info("Hash value ser: {}", redisTemplate.getHashValueSerializer().getClass());

        String key = m.getSensorType().toString() + "-motor:" + m.getMotorId();
        Map<String, Object> map = new HashMap<>();
        map.put("motorId", m.getMotorId());
        map.put("timestamp", m.getTimestamp());            // Instant serialized as JSON
        map.put("alertType", m.getAlertType().toString());            // enum/String ok
        map.put("sensorType", m.getSensorType().toString());
        map.put("value", m.getValue());                    // Double ok

        redisTemplate.opsForHash().putAll(key, map);

    }

}
