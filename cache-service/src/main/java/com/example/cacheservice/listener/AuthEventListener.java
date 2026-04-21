package com.example.cacheservice.listener;

import com.example.cacheservice.event.AuthEvent;
import com.example.cacheservice.service.RedisCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthEventListener.class);

    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;

    public AuthEventListener(ObjectMapper objectMapper, RedisCacheService redisCacheService) {
        this.objectMapper = objectMapper;
        this.redisCacheService = redisCacheService;
    }

    @KafkaListener(topics = "${app.kafka.auth-topic}", groupId = "${spring.application.name}")
    public void handleAuthEvent(String message) throws JsonProcessingException {
        log.info("Received Kafka auth event: {}", message);
        AuthEvent event = objectMapper.readValue(message, AuthEvent.class);
        redisCacheService.cacheAuthEvent(event);
    }
}
