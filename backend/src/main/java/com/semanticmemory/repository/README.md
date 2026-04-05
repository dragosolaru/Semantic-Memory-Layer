# Data Access Repositories

Spring Data JPA repositories.

## Expected Repositories

- `UserRepository.java`
- `WorkspaceRepository.java`
- `SourceRepository.java`
- `AssetRepository.java`
- `EntityRepository.java`
- `CollectionRepository.java`
- `SearchHistoryRepository.java`

## Conventions

- Extend `JpaRepository<Entity, UUID>`
- Custom queries via `@Query` annotations
- Use pgvector similarity via native queries for embedding search
- No business logic in repositories
