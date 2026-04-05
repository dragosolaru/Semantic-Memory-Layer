package com.semanticmemory.service;

import com.semanticmemory.exception.GlobalExceptionHandler.AuthException;
import com.semanticmemory.exception.GlobalExceptionHandler.NotFoundException;
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

/**
 * Authentication service.
 * 
 * Handles:
 * - User login with credentials
 * - User registration
 * - Password change with verification
 * - JWT token generation
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret:semantic-memory-secret-key-minimum-256-bits-for-hs256}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Authenticate user with email and password.
     * 
     * @param request Login credentials
     * @return JWT token and user data
     * @throws AuthException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
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
    
    /**
     * Register new user account.
     * 
     * @param request Registration data
     * @return JWT token and user data
     * @throws AuthException if email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already exists");
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

    /**
     * Change user password.
     * 
     * Requires current password verification.
     * 
     * @param request Password change data
     * @return Success message
     * @throws NotFoundException if user not found
     * @throws AuthException if current password is incorrect
     */
    public MessageResponse changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return new MessageResponse("Password changed successfully");
    }

    /**
     * Get user by ID.
     * 
     * @param id User UUID
     * @return Optional user
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(java.util.UUID.fromString(id));
    }
    
    /**
     * Generate JWT token for user.
     * 
     * @param user User entity
     * @return JWT token string
     */
    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        long expirationMs = jwtExpiration;
        
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
    }
}