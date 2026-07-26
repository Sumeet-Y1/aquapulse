package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.AuthResponse;
import com.aquapulse.backend.model.entity.User;
import com.aquapulse.backend.repository.UserRepository;
import com.aquapulse.backend.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public GoogleAuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(name != null ? name : email);
                newUser.setPassword(""); // no password for Google-auth users
                newUser.setRole(User.Role.RESIDENT);
                return userRepository.save(newUser);
            });

            String token = jwtUtil.generateToken(user.getEmail());
            return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());

        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Google token verification failed: " + e.getMessage());
        }
    }
}