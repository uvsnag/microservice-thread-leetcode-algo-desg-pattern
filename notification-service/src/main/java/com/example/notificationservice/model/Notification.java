package com.example.notificationservice.model;

import java.time.OffsetDateTime;

public record Notification(
        String id,
        String type,
        String source,
        String message,
        String occurredAt
) {
    public static Notification of(String type, String source, String message) {
        return new Notification(
                java.util.UUID.randomUUID().toString(),
                type,
                source,
                message,
                OffsetDateTime.now().toString()
        );
    }
}
