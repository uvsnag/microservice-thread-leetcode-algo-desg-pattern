package com.example.authservice.event;

public record AuthEvent(String action, String coCd, String usrId, String usrNm, boolean success, String reason, String occurredAt) {
}
