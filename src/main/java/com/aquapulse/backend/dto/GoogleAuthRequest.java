package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequest {

    @NotBlank
    private String idToken; // the ID token received from Google Sign-In on frontend
}