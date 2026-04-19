package com.semanticmemory.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SecurityConfigValidator {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private static final List<String> INSECURE_SECRETS = Arrays.asList(
            "semantic-memory-secret-key-minimum-256-bits-for-hs256",
            "semantic-memory-secret-key-minimum-256-bits-for-hs256-development",
            "change-this-secret-key",
            "secret-key",
            "password",
            "default-secret",
            "your-secret-key-here"
    );

    @PostConstruct
    public void validateJwtSecret() {
        boolean isDevelopment = "dev".equalsIgnoreCase(activeProfile) || "development".equalsIgnoreCase(activeProfile);
        
        if (INSECURE_SECRETS.stream().anyMatch(s -> jwtSecret.toLowerCase().contains(s.toLowerCase()))) {
            if (isDevelopment) {
                System.out.println("⚠️  WARNING: Using insecure JWT secret for development. Set JWT_SECRET for production.");
            } else {
                throw new IllegalStateException(
                        "SECURITY ALERT: JWT secret is set to an insecure default value. " +
                        "Please set a strong, unique jwt.secret in your configuration. " +
                        "Expected: minimum 256-bit random string for HS256."
                );
            }
        }

        if (jwtSecret.length() < 32) {
            throw new IllegalStateException(
                    "SECURITY ALERT: JWT secret must be at least 32 characters (256 bits) for HS256. " +
                    "Current length: " + jwtSecret.length()
            );
        }

        System.out.println("JWT Secret validation passed - secret is properly configured");
    }
}