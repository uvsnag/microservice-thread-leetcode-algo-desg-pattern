package com.example.cacheservice.event;

public record FileActivityEvent(String action, String folder, String filename, Integer resultCount, String occurredAt) {
}
