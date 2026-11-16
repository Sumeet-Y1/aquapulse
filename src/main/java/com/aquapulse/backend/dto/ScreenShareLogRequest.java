package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScreenShareLogRequest {

    // true = started sharing, false = stopped
    @NotNull
    private Boolean sharing;
}