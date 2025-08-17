package org.hmgics.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;


@Data
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

    @Schema(name = "motor_id", example = "MTR-01") // shown as motor_id in Swagger
    @JsonAlias("motor_id")
    @NotNull(message = "Motor ID must be valid")
    private String motorId;

    @NotNull(message = "Timestamp must be valid")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private Instant timestamp;

    @Schema(name = "vibration_g", example = "10") // shown as motor_id in Swagger
    @JsonAlias("vibration_g")
    @NotNull(message = "Vibration must be valid")
    @DecimalMin(value = "0.0", inclusive = true, message = "Vibration must be >= 0.0")
    private Double vibrationG;

    @Schema(name = "temperature_c", example = "100")
    @JsonAlias("temperature_c")
    @NotNull(message = "Temperature must be valid")
    @DecimalMin(value = "-50.0", inclusive = true, message = "Temperature must be >= -50.0")
    private Double temperatureC;

    @Schema(hidden = true)
    private SensorType sensorType;

    @Schema(hidden = true)
    private AlertType alertType;

    @Schema(hidden = true)
    private Double value;
}
