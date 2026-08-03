package com.aquapulse.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        log.info("Attempting to send OTP email to: {}", toEmail);
        log.info("Using sender: {} <{}>", senderName, senderEmail);
        log.info("API key present: {}, starts with: {}",
                brevoApiKey != null && !brevoApiKey.isBlank(),
                brevoApiKey != null && brevoApiKey.length() > 8 ? brevoApiKey.substring(0, 8) : "TOO_SHORT_OR_NULL");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        String subject = purpose.equals("EMAIL_VERIFICATION")
                ? "Verify your AquaPulse account"
                : "Reset your AquaPulse password";

        String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px;">
                    <h2 style="color: #2B6CB0;">%s</h2>
                    <p style="color: #3D4A5C; font-size: 15px;">Your verification code is:</p>
                    <div style="background: #EAF4FB; padding: 16px; border-radius: 12px; text-align: center; margin: 16px 0;">
                        <span style="font-size: 28px; font-weight: bold; letter-spacing: 6px; color: #1B2B45;">%s</span>
                    </div>
                    <p style="color: #8FA4C0; font-size: 13px;">This code expires in 10 minutes. If you didn't request this, you can safely ignore this email.</p>
                </div>
                """, subject, otpCode);

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(BREVO_URL, request, String.class);
            log.info("Brevo response: {}", response);
        } catch (RestClientException e) {
            log.error("Brevo API call failed", e);
            throw new IllegalArgumentException("Failed to send email: " + e.getMessage());
        }
    }
}