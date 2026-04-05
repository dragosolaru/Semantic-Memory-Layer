package com.semanticmemory;

import com.semanticmemory.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.semanticmemory.model.entity")
@EnableJpaRepositories("com.semanticmemory.repository")
@Import(SecurityConfig.class)
public class SemanticMemoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SemanticMemoryApplication.class, args);
    }
}