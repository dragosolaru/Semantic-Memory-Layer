package com.semanticmemory.repository;

import com.semanticmemory.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.user.id = :userId")
    int revokeAllUserTokens(UUID userId);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
    int deleteExpiredTokens();
}