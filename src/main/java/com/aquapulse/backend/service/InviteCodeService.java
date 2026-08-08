package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.InviteCodeResponse;
import com.aquapulse.backend.model.entity.InviteCode;
import com.aquapulse.backend.model.entity.Society;
import com.aquapulse.backend.repository.InviteCodeRepository;
import com.aquapulse.backend.repository.SocietyRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;
    private final SocietyRepository societyRepository;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public InviteCodeService(InviteCodeRepository inviteCodeRepository, SocietyRepository societyRepository) {
        this.inviteCodeRepository = inviteCodeRepository;
        this.societyRepository = societyRepository;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder("AQP-");
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public InviteCodeResponse generateStandardCode(Long societyId) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        invalidateExisting(societyId, InviteCode.Type.STANDARD);

        String code;
        do {
            code = generateCode();
        } while (inviteCodeRepository.findByCode(code).isPresent());

        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        inviteCode.setSociety(society);
        inviteCode.setType(InviteCode.Type.STANDARD);
        inviteCode.setExpiresAt(LocalDateTime.now().plusHours(24));
        inviteCodeRepository.save(inviteCode);

        return new InviteCodeResponse(code, inviteCode.getExpiresAt(), "STANDARD");
    }

    public InviteCodeResponse generateQrCode(Long societyId) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new IllegalArgumentException("Society not found"));

        invalidateExisting(societyId, InviteCode.Type.QR);

        String code;
        do {
            code = generateCode();
        } while (inviteCodeRepository.findByCode(code).isPresent());

        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        inviteCode.setSociety(society);
        inviteCode.setType(InviteCode.Type.QR);
        inviteCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        inviteCodeRepository.save(inviteCode);

        return new InviteCodeResponse(code, inviteCode.getExpiresAt(), "QR");
    }

    public Society validateAndGetSociety(String code) {
        InviteCode inviteCode = inviteCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        if (inviteCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This invite code has expired");
        }

        return inviteCode.getSociety();
    }

    private void invalidateExisting(Long societyId, InviteCode.Type type) {
        inviteCodeRepository.findTopBySocietyIdAndTypeOrderByIdDesc(societyId, type)
                .ifPresent(existing -> {
                    existing.setExpiresAt(LocalDateTime.now());
                    inviteCodeRepository.save(existing);
                });
    }
}