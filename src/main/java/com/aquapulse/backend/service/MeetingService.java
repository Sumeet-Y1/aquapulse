package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.JoinMeetingResponse;
import com.aquapulse.backend.dto.MeetingRequest;
import com.aquapulse.backend.dto.MeetingResponse;
import com.aquapulse.backend.dto.ScreenShareLogRequest;
import com.aquapulse.backend.model.entity.*;
import com.aquapulse.backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final ScreenShareLogRepository screenShareLogRepository;
    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;
    private final JitsiJwtService jitsiJwtService;

    @Value("${jitsi.domain:meet.jit.si}")
    private String jitsiDomain;

    public MeetingService(MeetingRepository meetingRepository,
                          MeetingParticipantRepository participantRepository,
                          ScreenShareLogRepository screenShareLogRepository,
                          SocietyRepository societyRepository,
                          UserRepository userRepository,
                          JitsiJwtService jitsiJwtService) {
        this.meetingRepository = meetingRepository;
        this.participantRepository = participantRepository;
        this.screenShareLogRepository = screenShareLogRepository;
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
        this.jitsiJwtService = jitsiJwtService;
    }

    @Transactional
    public MeetingResponse create(MeetingRequest request, Long userId) {
        User admin = loadUser(userId);
        requireMember(admin, request.getSocietyId());

        Society society = societyRepository.findById(request.getSocietyId())
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .society(society)
                .createdBy(admin)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .roomId("aquapulse-" + UUID.randomUUID())
                .status(Meeting.Status.SCHEDULED)
                .type(request.getType())
                .allowScreenShare(request.getAllowScreenShare() == null || request.getAllowScreenShare())
                .build();

        return toResponse(meetingRepository.save(meeting));
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> getBySociety(Long societyId, Long userId) {
        requireMember(loadUser(userId), societyId);
        return meetingRepository.findBySocietyIdOrderByScheduledAtDesc(societyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingResponse getById(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        requireMember(loadUser(userId), meeting.getSociety().getId());
        return toResponse(meeting);
    }

    @Transactional
    public JoinMeetingResponse join(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        User user = loadUser(userId);
        requireMember(user, meeting.getSociety().getId());

        boolean isAdmin = user.getRole() == User.Role.ADMIN;

        if (meeting.getStatus() == Meeting.Status.ENDED
                || meeting.getStatus() == Meeting.Status.CANCELLED) {
            throw new IllegalStateException("Meeting is " + meeting.getStatus().name().toLowerCase());
        }

        if (meeting.getStatus() == Meeting.Status.SCHEDULED) {
            if (!isAdmin) {
                throw new IllegalStateException("Meeting is not live yet");
            }
            // admin opening the room starts the meeting
            meeting.setStatus(Meeting.Status.LIVE);
            meetingRepository.save(meeting);
        }

        MeetingParticipant participant = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseGet(() -> MeetingParticipant.builder()
                        .meeting(meeting)
                        .user(user)
                        .build());

        if (participant.getJoinedAt() == null) {
            participant.setJoinedAt(LocalDateTime.now());
        }
        participant.setLeftAt(null);
        participantRepository.save(participant);

        boolean screenShare = meeting.isAllowScreenShare() || isAdmin;

        if (jitsiJwtService.isEnabled()) {
            String jwt = jitsiJwtService.createToken(user, meeting.getRoomId(), isAdmin, screenShare);
            return new JoinMeetingResponse(
                    meeting.getId(),
                    "8x8.vc",
                    jitsiJwtService.getAppId() + "/" + meeting.getRoomId(),
                    user.getFullName(),
                    meeting.getType().name(),
                    screenShare,
                    isAdmin,
                    jwt
            );
        }

        return new JoinMeetingResponse(
                meeting.getId(),
                jitsiDomain,
                meeting.getRoomId(),
                user.getFullName(),
                meeting.getType().name(),
                screenShare,
                isAdmin,
                null
        );
    }

    @Transactional
    public void leave(Long meetingId, Long userId) {
        participantRepository.findByMeetingIdAndUserId(meetingId, userId).ifPresent(p -> {
            p.setLeftAt(LocalDateTime.now());
            participantRepository.save(p);
        });
    }

    @Transactional
    public MeetingResponse start(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        requireMember(loadUser(userId), meeting.getSociety().getId());

        if (meeting.getStatus() != Meeting.Status.SCHEDULED) {
            throw new IllegalStateException("Only a scheduled meeting can be started");
        }
        meeting.setStatus(Meeting.Status.LIVE);
        return toResponse(meetingRepository.save(meeting));
    }

    @Transactional
    public MeetingResponse end(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        requireMember(loadUser(userId), meeting.getSociety().getId());

        if (meeting.getStatus() != Meeting.Status.LIVE) {
            throw new IllegalStateException("Only a live meeting can be ended");
        }
        meeting.setStatus(Meeting.Status.ENDED);

        // close attendance for anyone still marked as present
        LocalDateTime now = LocalDateTime.now();
        for (MeetingParticipant p : participantRepository.findByMeetingId(meetingId)) {
            if (p.getJoinedAt() != null && p.getLeftAt() == null) {
                p.setLeftAt(now);
                participantRepository.save(p);
            }
        }
        return toResponse(meetingRepository.save(meeting));
    }

    @Transactional
    public void cancel(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        requireMember(loadUser(userId), meeting.getSociety().getId());

        if (meeting.getStatus() != Meeting.Status.SCHEDULED) {
            throw new IllegalStateException("Only a scheduled meeting can be cancelled");
        }
        meeting.setStatus(Meeting.Status.CANCELLED);
        meetingRepository.save(meeting);
    }

    @Transactional
    public void logScreenShare(Long meetingId, Long userId, ScreenShareLogRequest request) {
        Meeting meeting = findMeeting(meetingId);
        User user = loadUser(userId);
        requireMember(user, meeting.getSociety().getId());

        if (meeting.getStatus() != Meeting.Status.LIVE) {
            throw new IllegalStateException("Meeting is not live");
        }
        if (!meeting.isAllowScreenShare() && user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Screen sharing is disabled for this meeting");
        }

        if (request.getSharing()) {
            screenShareLogRepository.save(ScreenShareLog.builder()
                    .meeting(meeting)
                    .user(user)
                    .startedAt(LocalDateTime.now())
                    .build());
        } else {
            screenShareLogRepository
                    .findFirstByMeetingIdAndUserIdAndEndedAtIsNullOrderByStartedAtDesc(meetingId, userId)
                    .ifPresent(log -> {
                        log.setEndedAt(LocalDateTime.now());
                        screenShareLogRepository.save(log);
                    });
        }
    }

    // ---------- helpers ----------

    private Meeting findMeeting(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void requireMember(User user, Long societyId) {
        boolean member = user.getSocieties().stream()
                .anyMatch(s -> s.getId().equals(societyId));
        if (!member) {
            throw new AccessDeniedException("You are not a member of this society");
        }
    }

    private MeetingResponse toResponse(Meeting m) {
        return new MeetingResponse(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getSociety().getId(),
                m.getCreatedBy().getFullName(),
                m.getScheduledAt(),
                m.getDurationMinutes(),
                m.getStatus().name(),
                m.getType().name(),
                m.isAllowScreenShare()
        );
    }
}