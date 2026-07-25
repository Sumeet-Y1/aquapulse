package com.aquapulse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocietyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String city;
}