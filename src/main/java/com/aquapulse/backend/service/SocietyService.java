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

import java.util.List;

@Service
public class SocietyService {

    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;
    private final InviteCodeService inviteCodeService;

    public SocietyService(SocietyRepository societyRepository, UserRepository userRepository, InviteCodeService inviteCodeService) {
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
        this.inviteCodeService = inviteCodeService;
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

        Society saved = societyRepository.save(society);

        User currentUser = getCurrentUser();
        currentUser.getSocieties().add(saved);
        userRepository.save(currentUser);

        return toResponse(saved);
    }

    public SocietyResponse join(String code) {
        Society society = inviteCodeService.validateAndGetSociety(code);

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
        return new SocietyResponse(society.getId(), society.getName(), society.getAddress(), society.getCity());
    }
}