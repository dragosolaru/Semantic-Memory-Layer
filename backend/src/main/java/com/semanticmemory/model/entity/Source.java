package com.semanticmemory.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Source {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;
    
    @Enumerated(EnumType.STRING)
    private SourceType type;
    
    @Column(nullable = false)
    private String pathOrIdentifier;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SourceStatus status = SourceStatus.PENDING;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime lastIndexedAt;
    
    @Builder.Default
    private Integer assetCount = 0;
    
    public enum SourceType {
        LOCAL_FOLDER, ICLOUD, GOOGLE_DRIVE, DROPBOX, EMAIL
    }
    
    public enum SourceStatus {
        ACTIVE, PAUSED, ERROR, PENDING
    }
}