package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocietyResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String inviteCode;
}