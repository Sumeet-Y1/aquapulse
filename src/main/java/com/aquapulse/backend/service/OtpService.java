package com.aquapulse.backend.service;

import com.aquapulse.backend.model.entity.OtpCode;
import com.aquapulse.backend.repository.OtpCodeRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpCodeRepository otpCodeRepository, EmailService emailService) {
        this.otpCodeRepository = otpCodeRepository;
        this.emailService = emailService;
    }

    public void generateAndSend(String email, OtpCode.Purpose purpose) {
        String code = String.format("%06d", random.nextInt(1_000_000));

        OtpCode otp = new OtpCode();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setPurpose(purpose);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setUsed(false);
        otpCodeRepository.save(otp);

        emailService.sendOtpEmail(email, code, purpose.name());
    }

    public void verify(String email, String code, OtpCode.Purpose purpose) {
        OtpCode otp = otpCodeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByIdDesc(email, purpose)
                .orElseThrow(() -> new IllegalArgumentException("No OTP found for this email. Please request a new one."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (!otp.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid OTP code.");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);
    }
}