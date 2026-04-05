# Backend - Spring Boot API

## Prerequisites

- Java 17+
- Maven

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

### Default (H2 File-Based)

By default, the application uses an H2 file-based database stored in `backend/data/`. This is automatically created on first run.

**Configuration:**
```properties
spring.datasource.url=jdbc:h2:file:./data/semanticmemory;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

The database file will be created at: `backend/data/semanticmemory.mv.db`

**Access H2 Console:**
1. Add to `application.properties`: `spring.h2.console.enabled=true`
2. Navigate to: `http://localhost:8080/h2-console`
3. JDBC URL: `jdbc:h2:file:./data/semanticmemory`

### PostgreSQL (Optional)

To use PostgreSQL instead of H2:

1. **Install PostgreSQL:**
   ```bash
   brew install postgresql@16
   brew services start postgresql@16
   ```

2. **Create Database:**
   ```bash
   createdb semanticmemory
   ```

3. **Configure Environment Variables:**
   ```bash
   export DATABASE_URL=jdbc:postgresql://localhost:5432/semanticmemory
   export DATABASE_USERNAME=postgres
   export DATABASE_PASSWORD=postgres
   ```

   Or modify `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/semanticmemory
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

4. **Add PostgreSQL dependency** (if not already in pom.xml):
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
   </dependency>
   ```

### Access with pgAdmin4

1. Create a new server in pgAdmin4
2. Configure connection:
   - Host: `localhost`
   - Port: `5432`
   - Database: `semanticmemory` (or the database name you created)
   - Username: `postgres` (or your PostgreSQL username)
   - Password: `postgres` (or your PostgreSQL password)

### Database Tables

The following tables are automatically created (via JPA Hibernate):
- `users` - User accounts
- `memories` - Stored memories
- `memory_tags` - Many-to-many relationship between memories and tags
- `tags` - Memory tags

Uses JPA with automatic schema creation (`spring.jpa.hibernate.ddl-auto=create-drop`). For production, use Flyway migrations.
