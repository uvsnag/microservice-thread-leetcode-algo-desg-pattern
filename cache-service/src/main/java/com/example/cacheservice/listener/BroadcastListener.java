package com.example.cacheservice.listener;

import com.example.cacheservice.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BroadcastListener {

    private static final Logger log = LoggerFactory.getLogger(BroadcastListener.class);

    private final RedisCacheService redisCacheService;

    public BroadcastListener(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @RabbitListener(queues = "system.broadcast.cache.queue")
    public void handleBroadcast(String message) {
        log.info("Cache: received broadcast message: {}", message);
        redisCacheService.saveEntry("broadcast::latest", message);
    }
}
