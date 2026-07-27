package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SocietyRepository extends JpaRepository<Society, Long> {
    Optional<Society> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
}