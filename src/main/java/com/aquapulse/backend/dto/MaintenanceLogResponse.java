package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MaintenanceLogResponse {
    private Long id;
    private LocalDate maintenanceDate;
    private String type;
    private String status;
    private String notes;
    private LocalDate nextDueDate;
    private Long unitId;
}