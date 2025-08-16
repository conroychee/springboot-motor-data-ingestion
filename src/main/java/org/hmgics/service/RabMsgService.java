package org.hmgics.service;

import org.hmgics.config.RabbitMQConfig;
import org.hmgics.model.MotorNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
public class RabMsgService {

    private final RabbitTemplate rabbitTemplate;

    public RabMsgService(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter converter){
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(converter);
    }

    public void publishMessage(MotorNotification alertMessage){
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,alertMessage);
        System.out.println("Sent: " + alertMessage);
    }
}