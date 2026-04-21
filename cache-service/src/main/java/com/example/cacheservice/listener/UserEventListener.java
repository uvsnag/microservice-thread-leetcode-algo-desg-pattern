package com.example.cacheservice.listener;

import com.example.cacheservice.event.UserEvent;
import com.example.cacheservice.service.RedisCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;

    public UserEventListener(ObjectMapper objectMapper, RedisCacheService redisCacheService) {
        this.objectMapper = objectMapper;
        this.redisCacheService = redisCacheService;
    }

    @KafkaListener(topics = "${app.kafka.user-topic}", groupId = "${spring.application.name}")
    public void handleUserEvent(String message) throws JsonProcessingException {
        log.info("Received Kafka user event: {}", message);
        redisCacheService.cacheUserEvent(objectMapper.readValue(message, UserEvent.class));
    }
}
