package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findBySocietyIdOrderByScheduledAtDesc(Long societyId);
}