package com.aquapulse.backend.controller;

import com.aquapulse.backend.service.AIInsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AIInsightController {

    private final AIInsightService aiInsightService;

    public AIInsightController(AIInsightService aiInsightService) {
        this.aiInsightService = aiInsightService;
    }

    @GetMapping("/api/insights/unit/{unitId}")
    public ResponseEntity<Map<String, String>> getInsight(@PathVariable Long unitId) {
        String insight = aiInsightService.generateInsight(unitId);
        return ResponseEntity.ok(Map.of("insight", insight));
    }
}