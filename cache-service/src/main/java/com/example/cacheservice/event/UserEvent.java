package com.example.cacheservice.event;

public record UserEvent(String action, String coCd, String usrId, String keyword, Integer resultCount, String occurredAt) {
}
