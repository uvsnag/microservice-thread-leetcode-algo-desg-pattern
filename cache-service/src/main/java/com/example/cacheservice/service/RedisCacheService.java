package com.example.cacheservice.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.cacheservice.event.AuthEvent;
import com.example.cacheservice.event.FileActivityEvent;
import com.example.cacheservice.event.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {

    private static final String ENTRY_PREFIX = "entries::";
    private static final String USER_PREFIX = "users::";
    private static final String FILE_ACTIVITY_KEY = "files::activity";
    private static final String AUTH_ACTIVITY_KEY = "auth::activity";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveEntry(String key, String value) {
        redisTemplate.opsForValue().set(ENTRY_PREFIX + key, value);
    }

    public String getEntry(String key) {
        return redisTemplate.opsForValue().get(ENTRY_PREFIX + key);
    }

    public Map<String, String> getEntries() {
        Set<String> keys = redisTemplate.keys(ENTRY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> entries = new LinkedHashMap<>();
        for (String key : new TreeSet<>(keys)) {
            String cleanKey = key.replaceFirst("^" + ENTRY_PREFIX, "");
            entries.put(cleanKey, redisTemplate.opsForValue().get(key));
        }
        return entries;
    }

    public String getCachedUser(String id) {
        return redisTemplate.opsForValue().get(USER_PREFIX + id);
    }

    public List<String> getRecentFileActivities() {
        List<String> activities = redisTemplate.opsForList().range(FILE_ACTIVITY_KEY, 0, 24);
        return activities == null ? Collections.emptyList() : activities;
    }

    public void cacheUserEvent(UserEvent userEvent) {
        String key = USER_PREFIX + userEvent.coCd() + ":" + userEvent.usrId();
        redisTemplate.opsForValue().set(key, writeAsJson(userEvent));
    }

    public void recordFileActivity(FileActivityEvent fileActivityEvent) {
        redisTemplate.opsForList().leftPush(FILE_ACTIVITY_KEY, writeAsJson(fileActivityEvent));
        redisTemplate.opsForList().trim(FILE_ACTIVITY_KEY, 0, 24);
    }

    public void cacheAuthEvent(AuthEvent authEvent) {
        redisTemplate.opsForList().leftPush(AUTH_ACTIVITY_KEY, writeAsJson(authEvent));
        redisTemplate.opsForList().trim(AUTH_ACTIVITY_KEY, 0, 49);
    }

    public List<String> getRecentAuthActivities() {
        List<String> activities = redisTemplate.opsForList().range(AUTH_ACTIVITY_KEY, 0, 49);
        return activities == null ? Collections.emptyList() : activities;
    }

    private String writeAsJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize cache payload", e);
        }
    }
}
