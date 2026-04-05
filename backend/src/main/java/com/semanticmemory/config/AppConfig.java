package com.semanticmemory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Value("${DATABASE_URL:jdbc:postgresql://localhost:5432/semanticmemory}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:postgres}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD:postgres}")
    private String databasePassword;

    @Value("${JWT_SECRET:semantic-memory-secret-key-minimum-256-bits-for-hs256}")
    private String jwtSecret;

    @Value("${OPENAI_API_KEY:sk-placeholder}")
    private String openAiApiKey;

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}