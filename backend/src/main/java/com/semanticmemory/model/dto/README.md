# Request/Response DTOs

Data Transfer Objects for API boundaries.

## Implemented DTOs

**Requests:**
- `LoginRequest.java`
- `RegisterRequest.java`
- `ChangePasswordRequest.java` - Email, current password, new password

**Responses:**
- `AuthResponse.java` - Token, user info
- `MessageResponse.java` - Simple message response

## Expected DTOs

**Requests:**
- `SearchRequest.java` - Query text, filters, pagination
- `IndexingRequest.java` - Asset metadata submission
- `SourceCreateRequest.java`

**Responses:**
- `AssetResponse.java` - Asset detail with metadata
- `SearchResponse.java` - Results list, ranking info, total count
- `SearchResultItem.java` - Single result with score
- `SourceResponse.java` - Source status and stats
- `PaginatedResponse.java` - Generic pagination wrapper

## Conventions

- Use records (Java 14+) for immutable DTOs
- Validate with Bean Validation annotations
- Never expose entities directly — always map to DTOs
- Use MapStruct or manual mapping (no reflection-based mappers)
