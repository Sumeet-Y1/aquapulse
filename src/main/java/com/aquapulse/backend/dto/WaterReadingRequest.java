package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WaterReadingRequest {

    @NotNull
    private LocalDate readingDate;

    @NotNull
    private Double waterCollectedLiters;

    @NotNull
    private Double storageLevelPercent;

    private Double rainfallMm;

    @NotNull
    private Long unitId;
}