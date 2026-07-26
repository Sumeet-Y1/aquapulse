package com.aquapulse.backend.dto;

import com.aquapulse.backend.model.entity.MaintenanceLog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceLogRequest {

    @NotNull
    private LocalDate maintenanceDate;

    @NotBlank
    private String type;

    @NotNull
    private MaintenanceLog.Status status;

    private String notes;

    private LocalDate nextDueDate;

    @NotNull
    private Long unitId;
}