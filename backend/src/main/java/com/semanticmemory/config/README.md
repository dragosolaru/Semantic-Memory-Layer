# Configuration

Spring configuration classes.

## Expected Configs

- `SecurityConfig.java` - Spring Security, JWT, CORS
- `WebConfig.java` - CORS, interceptors
- `DatabaseConfig.java` - DataSource, JPA, pgvector setup
- `RedisConfig.java` - Redis connection, cache config
- `AiServiceConfig.java` - OpenAI/AI service client config
- `AsyncConfig.java` - Async task executor config
- `JacksonConfig.java` - JSON serialization config

## Conventions

- One config class per concern
- Use `@ConfigurationProperties` for externalized config
- Never hardcode secrets — use environment variables
