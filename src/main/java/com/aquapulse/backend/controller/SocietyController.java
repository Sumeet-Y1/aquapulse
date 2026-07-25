package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.SocietyRequest;
import com.aquapulse.backend.dto.SocietyResponse;
import com.aquapulse.backend.service.SocietyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/societies")
public class SocietyController {

    private final SocietyService societyService;

    public SocietyController(SocietyService societyService) {
        this.societyService = societyService;
    }

    @PostMapping
    public ResponseEntity<SocietyResponse> create(@Valid @RequestBody SocietyRequest request) {
        return ResponseEntity.ok(societyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SocietyResponse>> getAll() {
        return ResponseEntity.ok(societyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocietyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(societyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocietyResponse> update(@PathVariable Long id, @Valid @RequestBody SocietyRequest request) {
        return ResponseEntity.ok(societyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        societyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}