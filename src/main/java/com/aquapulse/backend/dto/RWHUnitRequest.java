package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RWHUnitRequest {

    @NotNull
    private Double tankCapacityLiters;

    @NotNull
    private Double rooftopAreaSqm;

    private LocalDate installDate;

    @NotNull
    private Long societyId;
}