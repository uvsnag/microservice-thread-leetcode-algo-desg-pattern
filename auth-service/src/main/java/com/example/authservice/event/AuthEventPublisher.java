package com.example.authservice.event;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AuthEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public AuthEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.auth-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    public void publishLoginSuccess(String coCd, String usrId, String usrNm) {
        AuthEvent event = new AuthEvent("LOGIN_SUCCESS", coCd, usrId, usrNm, true, null, OffsetDateTime.now().toString());
        send(coCd + ":" + usrId, event);
    }

    public void publishLoginFailure(String coCd, String usrId, String reason) {
        AuthEvent event = new AuthEvent("LOGIN_FAILURE", coCd, usrId, null, false, reason, OffsetDateTime.now().toString());
        send(coCd + ":" + usrId, event);
    }

    public void publishTokenRefresh(String coCd, String usrId) {
        AuthEvent event = new AuthEvent("TOKEN_REFRESH", coCd, usrId, null, true, null, OffsetDateTime.now().toString());
        send(coCd + ":" + usrId, event);
    }

    public void publishTokenValidation(String usrId, String coCd, boolean valid) {
        String action = valid ? "TOKEN_VALID" : "TOKEN_INVALID";
        AuthEvent event = new AuthEvent(action, coCd, usrId, null, valid, null, OffsetDateTime.now().toString());
        send((coCd != null ? coCd : "unknown") + ":" + (usrId != null ? usrId : "unknown"), event);
    }

    private void send(String key, AuthEvent event) {
        try {
            kafkaTemplate.send(topicName, key, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize auth event", e);
        }
    }
}
