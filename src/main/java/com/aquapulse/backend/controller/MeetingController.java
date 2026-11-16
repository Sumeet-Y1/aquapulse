package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.JoinMeetingResponse;
import com.aquapulse.backend.dto.MeetingRequest;
import com.aquapulse.backend.dto.MeetingResponse;
import com.aquapulse.backend.dto.ScreenShareLogRequest;
import com.aquapulse.backend.security.CustomUserDetails;
import com.aquapulse.backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingResponse> create(@Valid @RequestBody MeetingRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.create(request, principal.getUser().getId()));
    }

    @GetMapping("/society/{societyId}")
    public ResponseEntity<List<MeetingResponse>> getBySociety(@PathVariable Long societyId,
                                                              @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.getBySociety(societyId, principal.getUser().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getById(@PathVariable Long id,
                                                   @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.getById(id, principal.getUser().getId()));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<JoinMeetingResponse> join(@PathVariable Long id,
                                                    @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.join(id, principal.getUser().getId()));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long id,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        meetingService.leave(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingResponse> start(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.start(id, principal.getUser().getId()));
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingResponse> end(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(meetingService.end(id, principal.getUser().getId()));
    }

    @PostMapping("/{id}/screenshare")
    public ResponseEntity<Void> screenShare(@PathVariable Long id,
                                            @Valid @RequestBody ScreenShareLogRequest request,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        meetingService.logScreenShare(id, principal.getUser().getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        meetingService.cancel(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}