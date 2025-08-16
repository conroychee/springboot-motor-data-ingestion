package org.hmgics.model;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

//@Entity
@Data
//@RedisHash
public class MotorNotification {

    public enum SensorType {
        TEMPERATURE_SENSOR,
        VIBRATION_SENSOR
    }

    public enum AlertType {
        VERY_HIGH_TEMPERATURE,
        HIGH_TEMPERATURE,
        VERY_HIGH_VIBRATION,
        HIGH_VIBRATION
    }


//    @Id
//    private String id;

    @NotNull(message = "Motor ID must be valid")
    private String motorId;

    @NotNull(message = "Timestamp must be valid")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private Instant timestamp;

    @NotNull(message = "Vibration must be valid")
    @DecimalMin(value = "0.0", inclusive = true, message = "Vibration must be >= 0.0")
//    @JsonProperty("vibration_g")
    private Double vibrationG;

    @NotNull(message = "Temperature must be valid")
    @DecimalMin(value = "-50.0", inclusive = true, message = "Temperature must be >= -50.0")
//    @JsonProperty("temperature_c")
    private Double temperatureC;

    private SensorType sensorType;

    private AlertType alertType;

    private Double value;
}
