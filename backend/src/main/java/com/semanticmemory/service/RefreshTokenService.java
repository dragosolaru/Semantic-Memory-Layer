package com.semanticmemory.service;

import com.semanticmemory.exception.GlobalExceptionHandler.AuthException;
import com.semanticmemory.model.dto.AuthResponse;
import com.semanticmemory.model.entity.RefreshToken;
import com.semanticmemory.model.entity.User;
import com.semanticmemory.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(RefreshToken::isValid)
                .orElseThrow(() -> new AuthException("Invalid or expired refresh token"));
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        RefreshToken rt = verifyRefreshToken(refreshToken);
        
        User user = rt.getUser();
        
        userService.generateToken(user);
        createRefreshToken(user);

        return AuthResponse.builder()
                .user(AuthResponse.UserResponse.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .subscriptionTier(user.getSubscriptionTier().name())
                        .build())
                .build();
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    rt.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(rt);
                });
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Transactional(readOnly = true)
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
    }
}