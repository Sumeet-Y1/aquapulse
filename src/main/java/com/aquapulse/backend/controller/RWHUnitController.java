package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.RWHUnitRequest;
import com.aquapulse.backend.dto.RWHUnitResponse;
import com.aquapulse.backend.service.RWHUnitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
public class RWHUnitController {

    private final RWHUnitService rwhUnitService;

    public RWHUnitController(RWHUnitService rwhUnitService) {
        this.rwhUnitService = rwhUnitService;
    }

    @PostMapping
    public ResponseEntity<RWHUnitResponse> create(@Valid @RequestBody RWHUnitRequest request) {
        return ResponseEntity.ok(rwhUnitService.create(request));
    }

    @GetMapping("/society/{societyId}")
    public ResponseEntity<List<RWHUnitResponse>> getBySociety(@PathVariable Long societyId) {
        return ResponseEntity.ok(rwhUnitService.getBySociety(societyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RWHUnitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rwhUnitService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RWHUnitResponse> update(@PathVariable Long id, @Valid @RequestBody RWHUnitRequest request) {
        return ResponseEntity.ok(rwhUnitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rwhUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}