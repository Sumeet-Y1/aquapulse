package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.*;
import com.aquapulse.backend.service.AuthService;
import com.aquapulse.backend.service.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
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