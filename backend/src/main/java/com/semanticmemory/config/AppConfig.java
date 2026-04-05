package com.semanticmemory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class AppConfig {

    @Value("${DATABASE_URL:jdbc:h2:file:./data/semanticmemory}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:sa}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD:}")
    private String databasePassword;

    @Value("${JWT_SECRET:semantic-memory-secret-key-minimum-256-bits-for-hs256}")
    private String jwtSecret;

    @Value("${OPENAI_API_KEY:sk-placeholder}")
    private String openAiApiKey;

    @Bean
    public DataSource dataSource() {
        try {
            if (databaseUrl.startsWith("jdbc:h2:file:")) {
                String path = databaseUrl.replace("jdbc:h2:file:", "").split(";")[0];
                Path dataDir = Path.of(path).getParent();
                if (dataDir != null && !Files.exists(dataDir)) {
                    Files.createDirectories(dataDir);
                    System.out.println("Created data directory: " + dataDir.toAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }

        return new org.springframework.jdbc.datasource.DriverManagerDataSource(
            databaseUrl,
            databaseUsername,
            databasePassword
        );
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
