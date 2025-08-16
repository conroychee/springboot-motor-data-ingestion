package org.hmgics.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hmgics.model.MotorNotification;
import org.hmgics.service.RabMsgService;
import org.hmgics.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

//@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MotorController {
//    @Autowired
//    private MachineRepository repository;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RabMsgService rabMsgService;


    @Operation(summary = "Put an alert details", description = "Receive the machine details." +
            "When the sensor value reaches threshold, it will publish to Rabbit MQ queue")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/sensor")
    public ResponseEntity<MotorNotification> create(@Valid @RequestBody
                                                    MotorNotification motorNotification) {
        String motorId = motorNotification.getMotorId();
        System.out.println("received={}, serverNow={}" + motorNotification.getTimestamp() +" ---" + java.time.Instant.now());
        if (motorNotification.getVibrationG() > 2.5) {
            motorNotification.setValue(motorNotification.getVibrationG());
            motorNotification.setSensorType(MotorNotification.SensorType.VIBRATION_SENSOR);
            motorNotification.setAlertType(
                    motorNotification.getVibrationG() > 5
                            ? MotorNotification.AlertType.VERY_HIGH_VIBRATION
                            : MotorNotification.AlertType.HIGH_VIBRATION);

            rabMsgService.publishMessage(motorNotification);
        }
        else{
            Optional<String> matchedKey = redisCacheService.findMatchedKey("VIBRATION_SENSOR-motor:"+motorId);
            System.out.println("Temperature -- " + matchedKey);
            matchedKey.ifPresent(redisCacheService::deleteMatchedKey);




        }
        if (motorNotification.getTemperatureC() > 80) {
            motorNotification.setValue(motorNotification.getTemperatureC());
            motorNotification.setSensorType(MotorNotification.SensorType.TEMPERATURE_SENSOR);
            motorNotification.setSensorType(MotorNotification.SensorType.TEMPERATURE_SENSOR);
            motorNotification.setAlertType(
                    motorNotification.getTemperatureC() > 150
                            ? MotorNotification.AlertType.VERY_HIGH_TEMPERATURE
                            : MotorNotification.AlertType.HIGH_TEMPERATURE);

            rabMsgService.publishMessage(motorNotification);
        }
        else{
            Optional<String> matchedKey  = redisCacheService.findMatchedKey("TEMPERATURE_SENSOR-motor:"+motorId);
            System.out.println("Temperature -- " + matchedKey);
            matchedKey.ifPresent(redisCacheService::deleteMatchedKey);
        }

        return ResponseEntity.ok(motorNotification);
        //return repository.save(machine);
    }
}

