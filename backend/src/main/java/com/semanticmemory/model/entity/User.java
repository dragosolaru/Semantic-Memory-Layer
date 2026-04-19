package com.semanticmemory.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String provider;
    
    private String providerId;
    
    private String firstName;
    
    private String lastName;
    
    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;
    
    @Transient
    public String getName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return null;
    }
    
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+", 2);
            this.firstName = parts[0];
            this.lastName = parts.length > 1 ? parts[1] : null;
        }
    }
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLoginAt;
    
    public enum SubscriptionTier {
        FREE, PERSONAL, FAMILY, PRO
    }
}