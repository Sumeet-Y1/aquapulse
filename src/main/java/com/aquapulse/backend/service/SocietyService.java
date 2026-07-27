package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.SocietyRequest;
import com.aquapulse.backend.dto.SocietyResponse;
import com.aquapulse.backend.model.entity.Society;
import com.aquapulse.backend.model.entity.User;
import com.aquapulse.backend.repository.SocietyRepository;
import com.aquapulse.backend.repository.UserRepository;
import com.aquapulse.backend.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class SocietyService {

    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public SocietyService(SocietyRepository societyRepository, UserRepository userRepository) {
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
    }

    private String generateInviteCode() {
        StringBuilder sb = new StringBuilder("AQP-");
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private User getCurrentUser() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(principal.getUser().getId()).orElseThrow();
    }

    public SocietyResponse create(SocietyRequest request) {
        Society society = new Society();
        society.setName(request.getName());
        society.setAddress(request.getAddress());
        society.setCity(request.getCity());

        String code;
        do {
            code = generateInviteCode();
        } while (societyRepository.existsByInviteCode(code));
        society.setInviteCode(code);

        Society saved = societyRepository.save(society);

        User currentUser = getCurrentUser();
        currentUser.getSocieties().add(saved);
        userRepository.save(currentUser);

        return toResponse(saved);
    }

    public SocietyResponse join(String inviteCode) {
        Society society = societyRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        User currentUser = getCurrentUser();
        if (currentUser.getSocieties().contains(society)) {
            throw new IllegalArgumentException("You've already joined this society");
        }
        currentUser.getSocieties().add(society);
        userRepository.save(currentUser);

        return toResponse(society);
    }

    public List<SocietyResponse> getMySocieties() {
        User currentUser = getCurrentUser();
        return currentUser.getSocieties().stream()
                .map(this::toResponse)
                .toList();
    }

    public SocietyResponse update(Long id, SocietyRequest request) {
        Society society = getOwnedSociety(id);

        society.setName(request.getName());
        society.setAddress(request.getAddress());
        society.setCity(request.getCity());

        return toResponse(societyRepository.save(society));
    }

    public void delete(Long id) {
        getOwnedSociety(id);
        societyRepository.deleteById(id);
    }

    private Society getOwnedSociety(Long id) {
        User currentUser = getCurrentUser();
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        if (!currentUser.getSocieties().contains(society)) {
            throw new IllegalArgumentException("You don't have access to this society");
        }
        return society;
    }

    private SocietyResponse toResponse(Society society) {
        return new SocietyResponse(society.getId(), society.getName(), society.getAddress(),
                society.getCity(), society.getInviteCode());
    }
}