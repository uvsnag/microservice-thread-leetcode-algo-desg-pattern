package com.example.notificationservice.listener;

import com.example.notificationservice.event.AuthEvent;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
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
    private final NotificationService notificationService;

    public AuthEventListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.auth-topic}", groupId = "notification-service")
    public void handleAuthEvent(String message) throws JsonProcessingException {
        log.info("Notification: received Kafka auth event: {}", message);
        AuthEvent event = objectMapper.readValue(message, AuthEvent.class);
        String msg = String.format("Auth action [%s] usrId=%s coCd=%s success=%s reason=%s",
                event.action(), event.usrId(), event.coCd(), event.success(), event.reason());
        notificationService.addNotification(Notification.of("AUTH_EVENT", "kafka", msg));
    }
}
