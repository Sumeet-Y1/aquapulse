package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.RWHUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RWHUnitRepository extends JpaRepository<RWHUnit, Long> {
    List<RWHUnit> findBySocietyId(Long societyId);
}