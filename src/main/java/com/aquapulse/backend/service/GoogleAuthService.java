package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.AuthResponse;
import com.aquapulse.backend.dto.GoogleAuthResponse;
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

    public GoogleAuthResponse authenticate(String idTokenString) {
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

            User existingUser = userRepository.findByEmail(email).orElse(null);

            if (existingUser != null) {
                // returning user — log them in normally
                String token = jwtUtil.generateToken(existingUser.getEmail());
                return new GoogleAuthResponse(
                        false, // needsRoleSelection
                        new AuthResponse(token, existingUser.getEmail(), existingUser.getFullName(), existingUser.getRole().name()),
                        null, null
                );
            } else {
                // brand new user — don't create account yet, ask frontend to collect role
                return new GoogleAuthResponse(true, null, email, name != null ? name : email);
            }

        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Google token verification failed: " + e.getMessage());
        }
    }

    public AuthResponse completeGoogleSignup(String email, String fullName, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Account already exists");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPassword("");
        newUser.setRole(role);
        newUser.setEmailVerified(true);
        userRepository.save(newUser);

        String token = jwtUtil.generateToken(newUser.getEmail());
        return new AuthResponse(token, newUser.getEmail(), newUser.getFullName(), newUser.getRole().name());
    }
}