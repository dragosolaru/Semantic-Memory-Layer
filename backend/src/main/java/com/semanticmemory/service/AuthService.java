package com.semanticmemory.service;

import com.semanticmemory.exception.GlobalExceptionHandler.AuthException;
import com.semanticmemory.exception.GlobalExceptionHandler.NotFoundException;
import com.semanticmemory.model.dto.*;
import com.semanticmemory.model.entity.RefreshToken;
import com.semanticmemory.model.entity.User;
import com.semanticmemory.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final FileUploadService fileUploadService;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.fileUploadService = fileUploadService;
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        refreshTokenService.createRefreshToken(user);
        
        return AuthResponse.builder()
            .user(AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build())
            .build();
    }
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already exists");
        }
        
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .subscriptionTier(User.SubscriptionTier.FREE)
            .build();
        
        user = userRepository.save(user);
        
        refreshTokenService.createRefreshToken(user);
        
        return AuthResponse.builder()
            .user(AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build())
            .build();
    }

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

    public Optional<User> getUserById(String id) {
        return userRepository.findById(java.util.UUID.fromString(id));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("role", "ROLE_" + user.getSubscriptionTier().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key)
            .compact();
    }
    
    public AuthResponse.UserResponse updateProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AuthException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        
        userRepository.save(user);
        
        return AuthResponse.UserResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .name(user.getName())
            .profileImageUrl(user.getProfileImageUrl())
            .subscriptionTier(user.getSubscriptionTier().name())
            .build();
    }
    
    public AuthResponse.UserResponse updateProfileImage(String userId, String imageUrl) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        fileUploadService.deleteOldImageIfExists(user.getProfileImageUrl());
        
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
        
        return AuthResponse.UserResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .name(user.getName())
            .profileImageUrl(user.getProfileImageUrl())
            .subscriptionTier(user.getSubscriptionTier().name())
            .build();
    }
    
    public AuthResponse.UserResponse uploadProfileImage(String userId, MultipartFile file) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        try {
            fileUploadService.deleteOldImageIfExists(user.getProfileImageUrl());
            
            String imageUrl = fileUploadService.uploadProfileImage(file, userId);
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
            
            return AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build();
        } catch (Exception e) {
            throw new AuthException("Failed to upload image: " + e.getMessage());
        }
    }
    
    public AuthResponse.UserResponse deleteProfileImage(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        fileUploadService.deleteOldImageIfExists(user.getProfileImageUrl());
        
        user.setProfileImageUrl(null);
        userRepository.save(user);
        
        return AuthResponse.UserResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .name(user.getName())
            .profileImageUrl(null)
            .subscriptionTier(user.getSubscriptionTier().name())
            .build();
    }
}