package com.aquapulse.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Double getRainfallMm(String city) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                city, apiKey
        );

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("rain")) {
                Map<String, Object> rain = (Map<String, Object>) response.get("rain");
                Object oneHour = rain.get("1h");
                return oneHour != null ? Double.parseDouble(oneHour.toString()) : 0.0;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0; // no rain data available or API error
        }
    }
}