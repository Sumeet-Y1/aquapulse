package com.aquapulse.backend.dto;

import com.aquapulse.backend.model.entity.Meeting;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetingRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Long societyId;

    @NotNull
    private LocalDateTime scheduledAt;

    @Min(1)
    private int durationMinutes;

    @NotNull
    private Meeting.Type type;

    // optional, defaults to true when omitted
    private Boolean allowScreenShare;
}