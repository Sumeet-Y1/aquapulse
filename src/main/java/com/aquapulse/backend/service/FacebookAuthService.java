package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.AuthResponse;
import com.aquapulse.backend.dto.GoogleAuthResponse;
import com.aquapulse.backend.model.entity.User;
import com.aquapulse.backend.repository.UserRepository;
import com.aquapulse.backend.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FacebookAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    public FacebookAuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public GoogleAuthResponse authenticate(String accessToken) {
        String url = String.format(
                "https://graph.facebook.com/me?fields=id,name,email&access_token=%s",
                accessToken
        );

        Map<String, Object> profile;
        try {
            profile = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Facebook access token");
        }

        if (profile == null || !profile.containsKey("email")) {
            throw new IllegalArgumentException("Facebook account has no email, or permission was not granted");
        }

        String email = (String) profile.get("email");
        String name = (String) profile.get("name");

        User existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser != null) {
            String token = jwtUtil.generateToken(existingUser.getEmail());
            return new GoogleAuthResponse(
                    false,
                    new AuthResponse(token, existingUser.getEmail(), existingUser.getFullName(), existingUser.getRole().name()),
                    null, null
            );
        } else {
            return new GoogleAuthResponse(true, null, email, name != null ? name : email);
        }
    }
}