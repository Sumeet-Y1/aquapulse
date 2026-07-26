package com.aquapulse.backend.service;

import com.aquapulse.backend.model.entity.WaterReading;
import com.aquapulse.backend.repository.WaterReadingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

@Service
public class AIInsightService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WaterReadingRepository waterReadingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public AIInsightService(WaterReadingRepository waterReadingRepository) {
        this.waterReadingRepository = waterReadingRepository;
    }

    public String generateInsight(Long unitId) {
        List<WaterReading> readings = waterReadingRepository.findByUnitIdOrderByReadingDateAsc(unitId);

        if (readings.isEmpty()) {
            return "No readings available yet for this unit.";
        }

        StringBuilder dataSummary = new StringBuilder();
        for (WaterReading r : readings) {
            dataSummary.append(String.format(
                    "Date: %s, Collected: %.1fL, Storage: %.1f%%, Rainfall: %.1fmm%n",
                    r.getReadingDate(), r.getWaterCollectedLiters(), r.getStorageLevelPercent(), r.getRainfallMm()
            ));
        }

        String prompt = "You are analyzing rainwater harvesting data for a residential unit. "
                + "Based on the following readings, give a short (3-4 sentence) insight summary covering "
                + "collection trends, storage efficiency, and any concerns:\n\n" + dataSummary;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(GROQ_URL, request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString();
        } catch (Exception e) {
            return "AI insight generation failed: " + e.getMessage();
        }
    }
}