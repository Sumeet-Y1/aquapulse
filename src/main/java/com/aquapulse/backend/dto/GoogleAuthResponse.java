package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleAuthResponse {
    private boolean needsRoleSelection;
    private AuthResponse authResponse; // populated if login succeeded (existing user)
    private String pendingEmail;       // populated if new user, needs role selection
    private String pendingFullName;
}