package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.MaintenanceLogRequest;
import com.aquapulse.backend.dto.MaintenanceLogResponse;
import com.aquapulse.backend.model.entity.MaintenanceLog;
import com.aquapulse.backend.model.entity.RWHUnit;
import com.aquapulse.backend.repository.MaintenanceLogRepository;
import com.aquapulse.backend.repository.RWHUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceLogService {

    private final MaintenanceLogRepository maintenanceLogRepository;
    private final RWHUnitRepository rwhUnitRepository;

    public MaintenanceLogService(MaintenanceLogRepository maintenanceLogRepository, RWHUnitRepository rwhUnitRepository) {
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.rwhUnitRepository = rwhUnitRepository;
    }

    public MaintenanceLogResponse create(MaintenanceLogRequest request) {
        RWHUnit unit = rwhUnitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("RWH Unit not found"));

        MaintenanceLog log = new MaintenanceLog();
        log.setMaintenanceDate(request.getMaintenanceDate());
        log.setType(request.getType());
        log.setStatus(request.getStatus());
        log.setNotes(request.getNotes());
        log.setNextDueDate(request.getNextDueDate());
        log.setUnit(unit);

        MaintenanceLog saved = maintenanceLogRepository.save(log);
        return toResponse(saved);
    }

    public List<MaintenanceLogResponse> getByUnit(Long unitId) {
        return maintenanceLogRepository.findByUnitId(unitId).stream()
                .map(this::toResponse)
                .toList();
    }

    public MaintenanceLogResponse update(Long id, MaintenanceLogRequest request) {
        MaintenanceLog log = maintenanceLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance log not found"));

        log.setMaintenanceDate(request.getMaintenanceDate());
        log.setType(request.getType());
        log.setStatus(request.getStatus());
        log.setNotes(request.getNotes());
        log.setNextDueDate(request.getNextDueDate());

        return toResponse(maintenanceLogRepository.save(log));
    }

    public void delete(Long id) {
        if (!maintenanceLogRepository.existsById(id)) {
            throw new IllegalArgumentException("Maintenance log not found");
        }
        maintenanceLogRepository.deleteById(id);
    }

    private MaintenanceLogResponse toResponse(MaintenanceLog log) {
        return new MaintenanceLogResponse(
                log.getId(),
                log.getMaintenanceDate(),
                log.getType(),
                log.getStatus().name(),
                log.getNotes(),
                log.getNextDueDate(),
                log.getUnit().getId()
        );
    }
}