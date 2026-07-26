package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.WaterReadingRequest;
import com.aquapulse.backend.dto.WaterReadingResponse;
import com.aquapulse.backend.model.entity.RWHUnit;
import com.aquapulse.backend.model.entity.WaterReading;
import com.aquapulse.backend.repository.RWHUnitRepository;
import com.aquapulse.backend.repository.WaterReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaterReadingService {

    private final WaterReadingRepository waterReadingRepository;
    private final RWHUnitRepository rwhUnitRepository;

    public WaterReadingService(WaterReadingRepository waterReadingRepository, RWHUnitRepository rwhUnitRepository) {
        this.waterReadingRepository = waterReadingRepository;
        this.rwhUnitRepository = rwhUnitRepository;
    }

    public WaterReadingResponse create(WaterReadingRequest request) {
        RWHUnit unit = rwhUnitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("RWH Unit not found"));

        WaterReading reading = new WaterReading();
        reading.setReadingDate(request.getReadingDate());
        reading.setWaterCollectedLiters(request.getWaterCollectedLiters());
        reading.setStorageLevelPercent(request.getStorageLevelPercent());
        reading.setRainfallMm(request.getRainfallMm());
        reading.setUnit(unit);

        WaterReading saved = waterReadingRepository.save(reading);
        return toResponse(saved);
    }

    public List<WaterReadingResponse> getByUnit(Long unitId) {
        return waterReadingRepository.findByUnitIdOrderByReadingDateAsc(unitId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!waterReadingRepository.existsById(id)) {
            throw new IllegalArgumentException("Reading not found");
        }
        waterReadingRepository.deleteById(id);
    }

    private WaterReadingResponse toResponse(WaterReading reading) {
        return new WaterReadingResponse(
                reading.getId(),
                reading.getReadingDate(),
                reading.getWaterCollectedLiters(),
                reading.getStorageLevelPercent(),
                reading.getRainfallMm(),
                reading.getUnit().getId()
        );
    }
}