package com.aquapulse.backend.dto;

import com.aquapulse.backend.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private User.Role role; // ADMIN or RESIDENT

    private Long societyId; // optional, resident links to existing society
}