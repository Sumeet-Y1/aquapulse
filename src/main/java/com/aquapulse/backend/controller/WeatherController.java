package com.aquapulse.backend.controller;

import com.aquapulse.backend.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather/rainfall")
    public ResponseEntity<Map<String, Object>> getRainfall(@RequestParam String city) {
        Double rainfall = weatherService.getRainfallMm(city);
        return ResponseEntity.ok(Map.of("city", city, "rainfallMm", rainfall));
    }
}