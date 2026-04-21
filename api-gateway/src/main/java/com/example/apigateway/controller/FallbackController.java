package com.example.apigateway.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, String>> fallbackGet() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "The requested service is temporarily unavailable. Please try again later.",
                        "status", "503"
                ));
    }

    @PostMapping("/fallback")
    public ResponseEntity<Map<String, String>> fallbackPost() {
        return fallbackGet();
    }
}
