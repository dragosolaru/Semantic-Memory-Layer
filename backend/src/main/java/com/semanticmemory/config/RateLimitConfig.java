package com.semanticmemory.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("Rate limiting enabled for authentication endpoints");
    }

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::createNewBucket);
    }

    private Bucket createNewBucket(String key) {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}