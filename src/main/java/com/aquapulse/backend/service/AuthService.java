package com.aquapulse.backend.service;

import com.aquapulse.backend.dto.AuthResponse;
import com.aquapulse.backend.dto.LoginRequest;
import com.aquapulse.backend.dto.RegisterRequest;
import com.aquapulse.backend.model.entity.OtpCode;
import com.aquapulse.backend.model.entity.Society;
import com.aquapulse.backend.model.entity.User;
import com.aquapulse.backend.repository.SocietyRepository;
import com.aquapulse.backend.repository.UserRepository;
import com.aquapulse.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SocietyRepository societyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository,
                       SocietyRepository societyRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       OtpService otpService) {
        this.userRepository = userRepository;
        this.societyRepository = societyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.RESIDENT);
        user.setEmailVerified(false);

        if (request.getSocietyId() != null) {
            Society society = societyRepository.findById(request.getSocietyId())
                    .orElseThrow(() -> new IllegalArgumentException("Society not found"));
            user.getSocieties().add(society);
        }

        userRepository.save(user);
        otpService.generateAndSend(user.getEmail(), OtpCode.Purpose.EMAIL_VERIFICATION);

        return "Registration successful. Please check your email for a verification code.";
    }

    public AuthResponse verifyEmail(String email, String code) {
        otpService.verify(email, code, OtpCode.Purpose.EMAIL_VERIFICATION);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("This account is already verified.");
        }

        otpService.generateAndSend(email, OtpCode.Purpose.EMAIL_VERIFICATION);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            // deliberately don't reveal whether the email exists, for security
            return;
        }
        otpService.generateAndSend(email, OtpCode.Purpose.PASSWORD_RESET);
    }

    public void resetPassword(String email, String code, String newPassword) {
        otpService.verify(email, code, OtpCode.Purpose.PASSWORD_RESET);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}