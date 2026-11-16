package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MeetingResponse {
    private Long id;
    private String title;
    private String description;
    private Long societyId;
    private String createdByName;
    private LocalDateTime scheduledAt;
    private int durationMinutes;
    private String status;
    private String type;
    private boolean allowScreenShare;
}