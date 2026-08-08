package com.aquapulse.backend.service;

import com.aquapulse.backend.model.entity.InviteCode;
import com.aquapulse.backend.repository.InviteCodeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class QrCodeService {

    private final InviteCodeRepository inviteCodeRepository;
    private final String frontendUrl;

    public QrCodeService(InviteCodeRepository inviteCodeRepository, @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.inviteCodeRepository = inviteCodeRepository;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    public byte[] generateQrImage(String code) {
        try {
            String content = inviteCodeRepository.findByCode(code)
                    .map(inviteCode -> buildJoinUrl(inviteCode.getCode()))
                    .orElse(code);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalArgumentException("Failed to generate QR code: " + e.getMessage());
        }
    }

    private String buildJoinUrl(String code) {
        return frontendUrl + "/join?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
    }
}
