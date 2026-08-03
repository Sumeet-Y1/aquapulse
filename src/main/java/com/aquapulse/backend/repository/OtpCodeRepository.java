package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByEmailAndPurposeAndUsedFalseOrderByIdDesc(String email, OtpCode.Purpose purpose);
}