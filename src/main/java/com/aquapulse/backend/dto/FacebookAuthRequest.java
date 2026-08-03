package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacebookAuthRequest {

    @NotBlank
    private String accessToken; // token received from Facebook JS SDK on frontend
}