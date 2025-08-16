package org.hmgics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hmgics.model.MotorNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RedisCacheService {

    private final Jackson2HashMapper mapper = new Jackson2HashMapper(false); // no flatten
    private final RedisTemplate<String, MotorNotification> redisTemplate;

    @Autowired
    private ObjectMapper om;

    public RedisCacheService(RedisTemplate<String, MotorNotification> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
        System.out.println("hash value ser: {}" + redisTemplate.getHashValueSerializer().getClass());

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
