package com.example.notificationservice.listener;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BroadcastListener {

    private static final Logger log = LoggerFactory.getLogger(BroadcastListener.class);

    private final NotificationService notificationService;

    public BroadcastListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "system.broadcast.notification.queue")
    public void handleBroadcast(String message) {
        log.info("Notification: received broadcast message: {}", message);
        notificationService.addNotification(Notification.of("BROADCAST", "rabbitmq-fanout", message));
    }

    @RabbitListener(queues = "event.topic.all.queue")
    public void handleTopicEvent(String message) {
        log.info("Notification: received topic event: {}", message);
        notificationService.addNotification(Notification.of("TOPIC_EVENT", "rabbitmq-topic", message));
    }
}
