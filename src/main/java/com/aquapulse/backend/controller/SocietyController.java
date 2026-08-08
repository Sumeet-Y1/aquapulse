package com.aquapulse.backend.controller;

import com.aquapulse.backend.dto.SocietyRequest;
import com.aquapulse.backend.dto.SocietyResponse;
import com.aquapulse.backend.service.SocietyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.aquapulse.backend.service.InviteCodeService;
import com.aquapulse.backend.service.QrCodeService;
import com.aquapulse.backend.dto.InviteCodeResponse;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/societies")
public class SocietyController {

    private final SocietyService societyService;
    private final InviteCodeService inviteCodeService;
    private final QrCodeService qrCodeService;

    public SocietyController(SocietyService societyService, InviteCodeService inviteCodeService, QrCodeService qrCodeService) {
        this.societyService = societyService;
        this.inviteCodeService = inviteCodeService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocietyResponse> create(@Valid @RequestBody SocietyRequest request) {
        return ResponseEntity.ok(societyService.create(request));
    }

    @PostMapping("/join")
    public ResponseEntity<SocietyResponse> join(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(societyService.join(body.get("inviteCode")));
    }

    @GetMapping("/my")
    public ResponseEntity<List<SocietyResponse>> getMySocieties() {
        return ResponseEntity.ok(societyService.getMySocieties());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocietyResponse> update(@PathVariable Long id, @Valid @RequestBody SocietyRequest request) {
        return ResponseEntity.ok(societyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        societyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invite-code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InviteCodeResponse> generateInviteCode(@PathVariable Long id) {
        return ResponseEntity.ok(inviteCodeService.generateStandardCode(id));
    }

    @PostMapping("/{id}/qr-code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InviteCodeResponse> generateQrCode(@PathVariable Long id) {
        return ResponseEntity.ok(inviteCodeService.generateQrCode(id));
    }

    @GetMapping("/qr-image/{code}")
    public ResponseEntity<byte[]> getQrImage(@PathVariable String code) {
        byte[] image = qrCodeService.generateQrImage(code);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}