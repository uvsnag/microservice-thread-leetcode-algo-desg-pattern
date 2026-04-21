package com.example.notificationservice.listener;

import com.example.notificationservice.event.FileActivityEvent;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
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
    private final NotificationService notificationService;

    public FileActivityListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.rabbitmq.file-activity-queue}")
    public void handleFileActivity(String message) throws JsonProcessingException {
        log.info("Notification: received RabbitMQ file activity: {}", message);
        FileActivityEvent event = objectMapper.readValue(message, FileActivityEvent.class);
        String msg = String.format("File action [%s] folder=%s file=%s results=%s",
                event.action(), event.folder(), event.filename(), event.resultCount());
        notificationService.addNotification(Notification.of("FILE_EVENT", "rabbitmq", msg));
    }
}
