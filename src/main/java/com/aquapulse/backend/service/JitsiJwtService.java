package com.aquapulse.backend.service;

import com.aquapulse.backend.model.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JitsiJwtService {

    @Value("${jaas.app-id:}")
    private String appId;

    @Value("${jaas.key-id:}")
    private String keyId;

    @Value("${jaas.private-key:}")
    private String privateKeyBase64;

    public boolean isEnabled() {
        return !appId.isBlank() && !keyId.isBlank() && !privateKeyBase64.isBlank();
    }

    public String getAppId() {
        return appId;
    }

    public String createToken(User user, String roomId, boolean moderator, boolean screenShare) {
        Instant now = Instant.now();

        Map<String, Object> userCtx = Map.of(
                "id", String.valueOf(user.getId()),
                "name", user.getFullName(),
                "email", user.getEmail(),
                "moderator", moderator
        );
        Map<String, Object> features = Map.of(
                "livestreaming", false,
                "recording", false,
                "transcription", false,
                "outbound-call", false
        );

        return Jwts.builder()
                .header().keyId(keyId).type("JWT").and()
                .issuer("chat")
                .claim("aud", "jitsi")
                .subject(appId)
                .claim("room", roomId)
                .notBefore(Date.from(now.minusSeconds(10)))
                .expiration(Date.from(now.plusSeconds(3 * 60 * 60)))
                .claim("context", Map.of("user", userCtx, "features", features))
                .signWith(loadPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey() {
        try {
            String pem = new String(Base64.getDecoder().decode(privateKeyBase64.trim()));
            String body = pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(body);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalStateException("Invalid JaaS private key", e);
        }
    }
}