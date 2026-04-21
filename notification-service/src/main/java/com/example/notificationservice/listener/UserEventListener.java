package com.example.notificationservice.listener;

import com.example.notificationservice.event.UserEvent;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
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
    private final NotificationService notificationService;

    public UserEventListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.user-topic}", groupId = "notification-service")
    public void handleUserEvent(String message) throws JsonProcessingException {
        log.info("Notification: received Kafka user event: {}", message);
        UserEvent event = objectMapper.readValue(message, UserEvent.class);
        String msg = String.format("User action [%s] usrId=%s coCd=%s results=%s",
                event.action(), event.usrId(), event.coCd(), event.resultCount());
        notificationService.addNotification(Notification.of("USER_EVENT", "kafka", msg));
    }
}
