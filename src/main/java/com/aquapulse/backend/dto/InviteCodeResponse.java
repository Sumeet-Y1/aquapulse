package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class InviteCodeResponse {
    private String code;
    private OffsetDateTime expiresAt;
    private String type;
}
