package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    Optional<InviteCode> findByCode(String code);
    Optional<InviteCode> findTopBySocietyIdAndTypeOrderByIdDesc(Long societyId, InviteCode.Type type);
}