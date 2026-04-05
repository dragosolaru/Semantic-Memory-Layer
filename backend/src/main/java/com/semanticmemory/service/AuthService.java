package com.semanticmemory.service;

import com.semanticmemory.model.dto.*;
import com.semanticmemory.model.entity.User;
import com.semanticmemory.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${JWT_SECRET:semantic-memory-secret-key-minimum-256-bits-for-hs256}")
    private String jwtSecret;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return AuthResponse.builder()
            .token(generateToken(user))
            .user(AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build())
            .build();
    }
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .subscriptionTier(User.SubscriptionTier.FREE)
            .build();
        
        user = userRepository.save(user);
        
        return AuthResponse.builder()
            .token(generateToken(user))
            .user(AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build())
            .build();
    }
    
    public Optional<User> getUserById(String id) {
        return userRepository.findById(java.util.UUID.fromString(id));
    }
    
    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(key)
            .compact();
    }
}