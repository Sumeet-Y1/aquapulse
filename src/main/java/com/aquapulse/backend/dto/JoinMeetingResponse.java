package com.aquapulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinMeetingResponse {
    private Long meetingId;
    private String domain;
    private String roomId;
    private String displayName;
    private String type;
    private boolean allowScreenShare;
    private boolean moderator;
    private String jwt;   // null when JaaS isn't configured
}