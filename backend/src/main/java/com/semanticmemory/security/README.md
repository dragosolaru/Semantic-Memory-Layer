# Security

Authentication, authorization, and security configuration.

## Expected Classes

- `JwtTokenProvider.java` - JWT generation, validation, parsing
- `JwtAuthenticationFilter.java` - Request filter for JWT
- `SecurityUtils.java` - Security helper methods
- `UserDetailsServiceImpl.java` - Spring Security user details

## Conventions

- JWT-based auth, stateless
- Token expiration: short-lived access token + refresh token
- Password hashing: BCrypt
- CORS configured in config package, not here
