package org.hmgics;

import org.hmgics.model.MotorNotification;
import org.hmgics.service.RedisCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
class MotorCacheTest {

    //this test container will be shared for the whole class. The docker image is redis:7-alpine
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", REDIS::getHost);
        r.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    RedisCacheService redisCacheService;

    @Test
    @DisplayName("Test Redis insertion works")
    void putAllAndGet() {
        MotorNotification motorNotification = new MotorNotification();
        motorNotification.setMotorId("MTR-99");
        motorNotification.setValue(3.2);
        motorNotification.setAlertType(MotorNotification.AlertType.HIGH_VIBRATION);
        motorNotification.setTimestamp(Instant.now());
        motorNotification.setSensorType(MotorNotification.SensorType.VIBRATION_SENSOR);

        redisCacheService.upsert(motorNotification);
        assertThat(redisCacheService.findMatchedKey("VIBRATION_SENSOR-motor:MTR-99")).isEqualTo(Optional.of("VIBRATION_SENSOR-motor:MTR-99"));
    }
}
