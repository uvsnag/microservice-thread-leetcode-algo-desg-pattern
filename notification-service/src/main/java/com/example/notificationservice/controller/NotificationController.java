package com.example.notificationservice.controller;

import java.util.List;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notifications", description = "View recent event notifications (Kafka + RabbitMQ)")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get recent notifications")
    @GetMapping
    public List<Notification> getRecent(@RequestParam(defaultValue = "50") int limit) {
        return notificationService.getRecentNotifications(limit);
    }

    @Operation(summary = "Get notifications by type (USER_EVENT or FILE_EVENT)")
    @GetMapping("/by-type")
    public List<Notification> getByType(@RequestParam String type) {
        return notificationService.getByType(type);
    }

    @Operation(summary = "Get total notification count")
    @GetMapping("/count")
    public long count() {
        return notificationService.count();
    }
}
