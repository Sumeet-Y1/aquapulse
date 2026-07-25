package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    List<WaterReading> findByUnitIdOrderByReadingDateAsc(Long unitId);
}