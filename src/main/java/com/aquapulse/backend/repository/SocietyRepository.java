package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.Society;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocietyRepository extends JpaRepository<Society, Long> {
}