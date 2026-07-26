package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.WaterReadingRequest;
import com.aquapulse.backend.dto.WaterReadingResponse;
import com.aquapulse.backend.service.WaterReadingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
public class WaterReadingController {

    private final WaterReadingService waterReadingService;

    public WaterReadingController(WaterReadingService waterReadingService) {
        this.waterReadingService = waterReadingService;
    }

    @PostMapping
    public ResponseEntity<WaterReadingResponse> create(@Valid @RequestBody WaterReadingRequest request) {
        return ResponseEntity.ok(waterReadingService.create(request));
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<WaterReadingResponse>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(waterReadingService.getByUnit(unitId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waterReadingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}