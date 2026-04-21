package com.example.cacheservice.listener;

import com.example.cacheservice.event.FileActivityEvent;
import com.example.cacheservice.service.RedisCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FileActivityListener {

    private static final Logger log = LoggerFactory.getLogger(FileActivityListener.class);

    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;

    public FileActivityListener(ObjectMapper objectMapper, RedisCacheService redisCacheService) {
        this.objectMapper = objectMapper;
        this.redisCacheService = redisCacheService;
    }

    @RabbitListener(queues = "${app.rabbitmq.file-activity-queue}")
    public void handleFileActivity(String message) throws JsonProcessingException {
        log.info("Received RabbitMQ file activity: {}", message);
        redisCacheService.recordFileActivity(objectMapper.readValue(message, FileActivityEvent.class));
    }
}
