# Backend - Spring Boot API

## Prerequisites

- Java 17+
- Maven
- PostgreSQL 18+ (installed at `/Library/PostgreSQL/18/`)

## Security

### Authentication
- JWT-based authentication (stateless)
- Token expiration: 24 hours (configurable via `jwt.expiration`)
- User ID extracted from JWT claims (not headers for security)

### Endpoint Access

| Endpoint | Auth Required | Description |
|----------|-------------|-------------|
| `/api/health` | No | Health check (public) |
| `/api/auth/login` | No | User login (public) |
| `/api/auth/register` | No | User registration (public) |
| `/api/auth/change-password` | Yes | Change password |
| `/api/auth/logout` | Yes | User logout |
| `/api/search` | Yes | Semantic search |

### Security Features

1. **Global Exception Handler**
   - Custom exceptions with safe error messages
   - No internal server details exposed to clients
   - Proper HTTP status codes (401, 404, 500)

2. **CORS Configuration**
   ```properties
   app.cors.allowed-origins=http://localhost:3000
   ```
   - Wildcard (`*`) disabled
   - Configurable origins
   - Credentials supported

3. **JWT Security**
   - Tokens stored in memory (stateless)
   - User ID from JWT principal (not HTTP headers)
   - Token expiration configurable

## Project Structure

```
backend/src/main/java/com/semanticmemory/
├── config/
│   └── SecurityConfig.java       # Security configuration
├── controller/
│   ├── AuthController.java    # Authentication endpoints
│   ├── HealthController.java # Health check
│   └── SearchController.java # Search endpoints
├── exception/
│   └── GlobalExceptionHandler.java # Exception handling
├── model/
│   ├── dto/                # Request/Response DTOs
│   └── entity/            # JPA entities
├── repository/            # Data access
├── security/
│   └── JwtAuthenticationFilter.java # JWT filter
└── service/
    ├── AuthService.java    # Auth business logic
    └── SearchService.java # Search business logic
```

## Setup

```bash
cd backend

# Install dependencies
./mvnw clean install

# Run
mvn spring-boot:run
```

**Alternative with Maven Wrapper:**
```bash
cd backend
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJ...",
  "type": "Bearer",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "John Doe",
    "subscriptionTier": "FREE"
  }
}
```

#### Register
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}
```

#### Change Password
```bash
POST /api/auth/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "user@example.com",
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456"
}
```

#### Logout
```bash
POST /api/auth/logout
Authorization: Bearer <token>
```

### Search

```bash
POST /api/search
Authorization: Bearer <token>
Content-Type: application/json

{
  "query": "search term",
  "page": 0,
  "pageSize": 10
}
```

### Health Check

```bash
GET /api/health
```

## Error Responses

| Status | Error | Description |
|--------|-------|-------------|
| 400 | Validation error | Invalid request body |
| 401 | Invalid credentials | Login failed |
| 401 | Email already exists | Registration failed |
| 401 | Current password is incorrect | Password change failed |
| 404 | User not found | Resource not found |
| 500 | An unexpected error occurred | Server error (details hidden) |

Example error response:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | API port |
| `app.cors.allowed-origins` | http://localhost:3000 | CORS origins |
| `jwt.secret` | (required) | JWT signing secret |
| `jwt.expiration` | 86400000 | Token expiration (ms) |
| `spring.datasource.url` | jdbc:postgresql://localhost:5432/semanticmemory | Database URL |

## Database

### PostgreSQL (Default)

**Start PostgreSQL:**
```bash
/Library/PostgreSQL/18/bin/pg_ctl -D /Library/PostgreSQL/18/data start
```

**Create Database:**
```bash
PGPASSWORD=postgres /Library/PostgreSQL/18/bin/psql -h localhost -U postgres -c "CREATE DATABASE semanticmemory;"
```

### Database Tables

Auto-created via JPA Hibernate:
- `users` - User accounts
- `workspaces` - User workspaces
- `sources` - Data sources
- `assets` - Indexed files/documents