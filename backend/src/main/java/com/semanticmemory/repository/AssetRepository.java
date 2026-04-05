package com.semanticmemory.repository;

import com.semanticmemory.model.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    
    Page<Asset> findByUserId(UUID userId, Pageable pageable);
    
    Page<Asset> findByUserIdAndSourceId(UUID userId, UUID sourceId, Pageable pageable);
    
    // @Query("SELECT a FROM Asset a WHERE a.user.id = :userId ORDER BY FUNCTION('cosine_distance', a.embedding, :embedding) LIMIT :limit")
    // Page<Asset> findSimilarByEmbedding(@Param("userId") UUID userId, @Param("embedding") float[] embedding, @Param("limit") int limit, Pageable pageable);
    
    long countByUserId(UUID userId);
}