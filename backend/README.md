# Backend - Spring Boot API

## Prerequisites

- Java 17+
- PostgreSQL 15+ (running on localhost:5432)
- Maven

## Setup

```bash
# Install dependencies
cd backend
./mvnw clean install

# Run
./mvnw spring-boot:run
```

## API Endpoints

| Method | Path | Description |
|-------|------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |
| POST | /api/search | Search assets |
| GET | /api/health | Health check |

## Environment

Variables from `src/main/resources/application.properties`:
- `server.port` - API port (default 8080)
- `spring.datasource.url` - PostgreSQL URL
- `jwt.secret` - JWT signing secret

## Database

Uses JPA with automatic schema creation. For production, use Flyway migrations.