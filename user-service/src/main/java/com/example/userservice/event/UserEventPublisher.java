package com.example.userservice.event;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public UserEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.user-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    public void publishViewEvent(String coCd, String usrId) {
        UserEvent event = new UserEvent("VIEWED", coCd, usrId, null, 1, OffsetDateTime.now().toString());
        send(coCd + ":" + usrId, event);
    }

    public void publishSearchEvent(String action, String keyword, int resultCount) {
        UserEvent event = new UserEvent(action, null, null, keyword, resultCount, OffsetDateTime.now().toString());
        send(action, event);
    }

    private void send(String key, UserEvent event) {
        try {
            kafkaTemplate.send(topicName, key, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user event", e);
        }
    }
}
