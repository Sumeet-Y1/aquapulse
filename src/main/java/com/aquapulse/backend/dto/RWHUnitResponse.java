package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RWHUnitResponse {
    private Long id;
    private Double tankCapacityLiters;
    private Double rooftopAreaSqm;
    private LocalDate installDate;
    private Long societyId;
    private String societyName;
}