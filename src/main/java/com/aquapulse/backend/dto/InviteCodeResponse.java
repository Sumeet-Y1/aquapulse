package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InviteCodeResponse {
    private String code;
    private LocalDateTime expiresAt;
    private String type;
}