package com.semanticmemory;

import com.semanticmemory.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SecurityConfig.class)
public class SemanticMemoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SemanticMemoryApplication.class, args);
    }
}