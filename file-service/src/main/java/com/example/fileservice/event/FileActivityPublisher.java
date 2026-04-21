package com.example.fileservice.event;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileActivityPublisher {

    private static final Logger log = LoggerFactory.getLogger(FileActivityPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchangeName;
    private final String routingKey;

    public FileActivityPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Value("${app.rabbitmq.file-activity-exchange}") String exchangeName,
            @Value("${app.rabbitmq.file-activity-routing-key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void publish(String action, String folder, String filename, Integer resultCount) {
        FileActivityEvent event = new FileActivityEvent(action, folder, filename, resultCount, OffsetDateTime.now().toString());
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize file activity event", e);
        }
    }
}
