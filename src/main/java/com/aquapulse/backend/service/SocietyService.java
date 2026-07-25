package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.SocietyRequest;
import com.aquapulse.backend.dto.SocietyResponse;
import com.aquapulse.backend.model.entity.Society;
import com.aquapulse.backend.repository.SocietyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocietyService {

    private final SocietyRepository societyRepository;

    public SocietyService(SocietyRepository societyRepository) {
        this.societyRepository = societyRepository;
    }

    public SocietyResponse create(SocietyRequest request) {
        Society society = new Society();
        society.setName(request.getName());
        society.setAddress(request.getAddress());
        society.setCity(request.getCity());

        Society saved = societyRepository.save(society);
        return toResponse(saved);
    }

    public List<SocietyResponse> getAll() {
        return societyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public SocietyResponse getById(Long id) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));
        return toResponse(society);
    }

    public SocietyResponse update(Long id, SocietyRequest request) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        society.setName(request.getName());
        society.setAddress(request.getAddress());
        society.setCity(request.getCity());

        return toResponse(societyRepository.save(society));
    }

    public void delete(Long id) {
        if (!societyRepository.existsById(id)) {
            throw new IllegalArgumentException("Society not found");
        }
        societyRepository.deleteById(id);
    }

    private SocietyResponse toResponse(Society society) {
        return new SocietyResponse(society.getId(), society.getName(), society.getAddress(), society.getCity());
    }
}