package com.example.cacheservice.controller;

import java.util.List;
import java.util.Map;

import com.example.cacheservice.service.RedisCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Cache", description = "Redis cache management and event viewer")
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final RedisCacheService redisCacheService;
    private final RabbitTemplate rabbitTemplate;

    public CacheController(RedisCacheService redisCacheService, RabbitTemplate rabbitTemplate) {
        this.redisCacheService = redisCacheService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Operation(summary = "Set a cache entry")
    @PostMapping("/entries")
    public Map<String, String> setEntry(@RequestParam String key, @RequestParam String value) {
        redisCacheService.saveEntry(key, value);
        return Map.of("key", key, "value", value);
    }

    @Operation(summary = "Get a cache entry by key")
    @GetMapping("/entries/{key}")
    public Map<String, String> getEntry(@PathVariable String key) {
        String value = redisCacheService.getEntry(key);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache entry not found");
        }
        return Map.of("key", key, "value", value);
    }

    @Operation(summary = "Get all cache entries")
    @GetMapping("/entries")
    public Map<String, String> getEntries() {
        return redisCacheService.getEntries();
    }

    @Operation(summary = "Get cached user event by ID")
    @GetMapping("/users/{id}")
    public String getCachedUser(@PathVariable String id) {
        String cachedUser = redisCacheService.getCachedUser(id);
        if (cachedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cached user not found");
        }
        return cachedUser;
    }

    @Operation(summary = "Get recent file activity events")
    @GetMapping("/file-activities")
    public List<String> getRecentFileActivities() {
        return redisCacheService.getRecentFileActivities();
    }

    @Operation(summary = "Get recent auth activity events (login/refresh/validate)")
    @GetMapping("/auth-activities")
    public List<String> getRecentAuthActivities() {
        return redisCacheService.getRecentAuthActivities();
    }

    @Operation(summary = "Broadcast a message to all services via RabbitMQ Fanout Exchange")
    @PostMapping("/broadcast")
    public Map<String, String> broadcast(@RequestParam String message) {
        rabbitTemplate.convertAndSend("system.broadcast.exchange", "", message);
        return Map.of("status", "broadcasted", "message", message);
    }
}
