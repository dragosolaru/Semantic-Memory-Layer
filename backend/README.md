# Backend - Spring Boot API

## Prerequisites

- Java 17+
- Maven
- PostgreSQL 18+ (installed at `/Library/PostgreSQL/18/`)

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

| Method | Path | Description |
|-------|------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |
| POST | /api/auth/change-password | Change user password (requires JWT) |
| POST | /api/search | Search assets |
| GET | /api/health | Health check |

### Change Password

**Endpoint:** `POST /api/auth/change-password`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "email": "user@example.com",
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456"
}
```

**Response:** `200 OK`
```json
{
  "message": "Password changed successfully"
}
```

**Errors:**
- `400` - Current password is incorrect
- `404` - User not found

## Environment

Variables from `src/main/resources/application.properties`:
- `server.port` - API port (default 8080)
- `spring.datasource.url` - Database URL
- `jwt.secret` - JWT signing secret

## Database

### PostgreSQL (Default)

The application uses PostgreSQL as the default database.

**Prerequisites:**
- PostgreSQL 18 installed at `/Library/PostgreSQL/18/`
- Database `semanticmemory` created

**Start PostgreSQL:**
```bash
/Library/PostgreSQL/18/bin/pg_ctl -D /Library/PostgreSQL/18/data start
```

**Create Database:**
```bash
PGPASSWORD=postgres /Library/PostgreSQL/18/bin/psql -h localhost -U postgres -c "CREATE DATABASE semanticmemory;"
```

**Configuration (application.properties):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/semanticmemory
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### Access with pgAdmin4

1. Open pgAdmin4: `/Library/PostgreSQL/18/pgAdmin 4.app`
2. Create a new server:
   - Host: `localhost`
   - Port: `5432`
   - Database: `semanticmemory`
   - Username: `postgres`
   - Password: `postgres`

### Database Tables

The following tables are automatically created (via JPA Hibernate with `ddl-auto=update`):
- `users` - User accounts
- `workspaces` - User workspaces
- `sources` - Data sources
- `assets` - Indexed files/documents