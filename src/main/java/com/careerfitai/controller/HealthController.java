package com.careerfitai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "CareerFit AI Backend");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("message", "CareerFit AI API service is healthy and operational.");
        return ResponseEntity.ok(health);
    }
}
