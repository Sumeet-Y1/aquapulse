package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.RWHUnitRequest;
import com.aquapulse.backend.dto.RWHUnitResponse;
import com.aquapulse.backend.model.entity.RWHUnit;
import com.aquapulse.backend.model.entity.Society;
import com.aquapulse.backend.repository.RWHUnitRepository;
import com.aquapulse.backend.repository.SocietyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RWHUnitService {

    private final RWHUnitRepository rwhUnitRepository;
    private final SocietyRepository societyRepository;

    public RWHUnitService(RWHUnitRepository rwhUnitRepository, SocietyRepository societyRepository) {
        this.rwhUnitRepository = rwhUnitRepository;
        this.societyRepository = societyRepository;
    }

    public RWHUnitResponse create(RWHUnitRequest request) {
        Society society = societyRepository.findById(request.getSocietyId())
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        RWHUnit unit = new RWHUnit();
        unit.setTankCapacityLiters(request.getTankCapacityLiters());
        unit.setRooftopAreaSqm(request.getRooftopAreaSqm());
        unit.setInstallDate(request.getInstallDate());
        unit.setSociety(society);

        RWHUnit saved = rwhUnitRepository.save(unit);
        return toResponse(saved);
    }

    public List<RWHUnitResponse> getBySociety(Long societyId) {
        return rwhUnitRepository.findBySocietyId(societyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public RWHUnitResponse getById(Long id) {
        RWHUnit unit = rwhUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RWH Unit not found"));
        return toResponse(unit);
    }

    public RWHUnitResponse update(Long id, RWHUnitRequest request) {
        RWHUnit unit = rwhUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RWH Unit not found"));

        Society society = societyRepository.findById(request.getSocietyId())
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        unit.setTankCapacityLiters(request.getTankCapacityLiters());
        unit.setRooftopAreaSqm(request.getRooftopAreaSqm());
        unit.setInstallDate(request.getInstallDate());
        unit.setSociety(society);

        return toResponse(rwhUnitRepository.save(unit));
    }

    public void delete(Long id) {
        if (!rwhUnitRepository.existsById(id)) {
            throw new IllegalArgumentException("RWH Unit not found");
        }
        rwhUnitRepository.deleteById(id);
    }

    private RWHUnitResponse toResponse(RWHUnit unit) {
        return new RWHUnitResponse(
                unit.getId(),
                unit.getTankCapacityLiters(),
                unit.getRooftopAreaSqm(),
                unit.getInstallDate(),
                unit.getSociety().getId(),
                unit.getSociety().getName()
        );
    }
}