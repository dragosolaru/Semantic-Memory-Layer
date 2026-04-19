package com.semanticmemory.controller;

import com.semanticmemory.model.dto.*;
import com.semanticmemory.service.AuthService;
import com.semanticmemory.service.CookieService;
import com.semanticmemory.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, CookieService cookieService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.cookieService = cookieService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.login(request);
        
        String userId = authResponse.getUser().getId();
        authService.getUserById(userId).ifPresent(user -> {
            String accessToken = authService.generateToken(user);
            cookieService.setAccessTokenCookie(response, accessToken);
        });
        
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.register(request);
        
        String userId = authResponse.getUser().getId();
        authService.getUserById(userId).ifPresent(user -> {
            String accessToken = authService.generateToken(user);
            cookieService.setAccessTokenCookie(response, accessToken);
        });
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        
        authService.getUserById(authResponse.getUser().getId())
            .ifPresent(user -> {
                String accessToken = authService.generateToken(user);
                cookieService.setAccessTokenCookie(response, accessToken);
            });
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            Authentication authentication,
            HttpServletResponse response) {
        
        if (authentication != null && authentication.getPrincipal() != null) {
            String userId = authentication.getPrincipal().toString();
            refreshTokenService.revokeAllUserTokens(java.util.UUID.fromString(userId));
        }
        
        cookieService.clearAccessTokenCookie(response);
        cookieService.clearRefreshTokenCookie(response);
        
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        
        String userId = authentication.getPrincipal().toString();
        return authService.getUserById(userId)
            .map(user -> ResponseEntity.ok(AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionTier(user.getSubscriptionTier().name())
                .build()))
            .orElse(ResponseEntity.status(401).build());
    }
    
    @PutMapping("/profile")
    public ResponseEntity<AuthResponse.UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        
        String userId = authentication.getPrincipal().toString();
        AuthResponse.UserResponse updated = authService.updateProfile(userId, request);
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/profile/image")
    public ResponseEntity<AuthResponse.UserResponse> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        
        String userId = authentication.getPrincipal().toString();
        AuthResponse.UserResponse updated = authService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/profile/image")
    public ResponseEntity<AuthResponse.UserResponse> deleteProfileImage(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        
        String userId = authentication.getPrincipal().toString();
        AuthResponse.UserResponse updated = authService.deleteProfileImage(userId);
        return ResponseEntity.ok(updated);
    }
}