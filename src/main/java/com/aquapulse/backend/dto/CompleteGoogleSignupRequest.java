package com.aquapulse.backend.dto;

import com.aquapulse.backend.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteGoogleSignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotNull
    private User.Role role;
}