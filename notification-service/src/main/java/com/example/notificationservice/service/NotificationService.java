package com.example.notificationservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.example.notificationservice.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final int MAX_NOTIFICATIONS = 100;
    private final ConcurrentLinkedDeque<Notification> notifications = new ConcurrentLinkedDeque<>();

    public void addNotification(Notification notification) {
        notifications.addFirst(notification);
        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.removeLast();
        }
    }

    public List<Notification> getRecentNotifications(int limit) {
        List<Notification> result = new ArrayList<>();
        int count = 0;
        for (Notification n : notifications) {
            if (count >= limit) break;
            result.add(n);
            count++;
        }
        return Collections.unmodifiableList(result);
    }

    public List<Notification> getByType(String type) {
        return notifications.stream()
                .filter(n -> n.type().equalsIgnoreCase(type))
                .toList();
    }

    public long count() {
        return notifications.size();
    }
}
