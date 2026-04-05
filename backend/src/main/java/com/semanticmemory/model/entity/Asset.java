package com.semanticmemory.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private String fileName;
    
    @Enumerated(EnumType.STRING)
    private AssetType fileType;
    
    private String mimeType;
    private Long fileSizeBytes;
    
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    @CreationTimestamp
    private LocalDateTime indexedAt;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssetStatus status = AssetStatus.PENDING;
    
    private String checksum;
    private String thumbnailUrl;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private AssetMetadata metadata;
    
    @Column(columnDefinition = "TEXT")
    private String extractedText;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ExtractedEntity> entities;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private AssetClassification classification;
    
    public enum AssetType {
        IMAGE, PDF, DOCUMENT, SPREADSHEET, OTHER
    }
    
    public enum AssetStatus {
        INDEXED, PENDING, ERROR, DELETED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetMetadata {
        private String summary;
        private String location;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedEntity {
        private String type;
        private String value;
        private String source;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetClassification {
        private String category;
        private List<String> tags;
        private String domain;
        private Double confidenceScore;
    }
}