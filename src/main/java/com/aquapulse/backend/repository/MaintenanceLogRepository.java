package com.aquapulse.backend.repository;

import com.aquapulse.backend.model.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    List<MaintenanceLog> findByUnitId(Long unitId);
}