package com.aquapulse.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final long startTime = System.currentTimeMillis();

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "aquapulse-backend");
        response.put("timestamp", Instant.now().toString());
        response.put("uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000);

        // check database connectivity
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            response.put("database", "UP");
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("status", "DEGRADED");
        }

        // basic memory info
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemoryMB = runtime.maxMemory() / (1024 * 1024);
        response.put("memoryUsedMB", usedMemoryMB);
        response.put("memoryMaxMB", maxMemoryMB);

        return ResponseEntity.ok(response);
    }
}