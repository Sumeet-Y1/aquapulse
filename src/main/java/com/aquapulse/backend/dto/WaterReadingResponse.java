package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WaterReadingResponse {
    private Long id;
    private LocalDate readingDate;
    private Double waterCollectedLiters;
    private Double storageLevelPercent;
    private Double rainfallMm;
    private Long unitId;
}