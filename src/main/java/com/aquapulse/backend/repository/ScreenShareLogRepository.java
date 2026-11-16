package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.ScreenShareLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScreenShareLogRepository extends JpaRepository<ScreenShareLog, Long> {

    List<ScreenShareLog> findByMeetingId(Long meetingId);

    // The currently open share session for a user, used when they stop sharing
    Optional<ScreenShareLog> findFirstByMeetingIdAndUserIdAndEndedAtIsNullOrderByStartedAtDesc(
            Long meetingId, Long userId);
}