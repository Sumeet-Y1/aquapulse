package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.*;
import com.aquapulse.backend.service.AuthService;
import com.aquapulse.backend.service.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    public AuthController(AuthService authService, GoogleAuthService googleAuthService) {
        this.authService = authService;
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request.getEmail(), request.getCode()));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody Map<String, String> body) {
        authService.resendVerification(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "Verification code resent."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "If that email is registered, a reset code has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful. You can now log in."));
    }

    @PostMapping("/google")
    public ResponseEntity<GoogleAuthResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(googleAuthService.authenticate(request.getIdToken()));
    }

    @PostMapping("/google/complete")
    public ResponseEntity<AuthResponse> completeGoogleSignup(@Valid @RequestBody CompleteGoogleSignupRequest request) {
        return ResponseEntity.ok(googleAuthService.completeGoogleSignup(
                request.getEmail(), request.getFullName(), request.getRole()));
    }
}