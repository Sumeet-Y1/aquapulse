package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.MaintenanceLogRequest;
import com.aquapulse.backend.dto.MaintenanceLogResponse;
import com.aquapulse.backend.service.MaintenanceLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceLogController {

    private final MaintenanceLogService maintenanceLogService;

    public MaintenanceLogController(MaintenanceLogService maintenanceLogService) {
        this.maintenanceLogService = maintenanceLogService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceLogResponse> create(@Valid @RequestBody MaintenanceLogRequest request) {
        return ResponseEntity.ok(maintenanceLogService.create(request));
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<MaintenanceLogResponse>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(maintenanceLogService.getByUnit(unitId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceLogResponse> update(@PathVariable Long id, @Valid @RequestBody MaintenanceLogRequest request) {
        return ResponseEntity.ok(maintenanceLogService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}