# JPA Entity Classes

Database entity definitions.

## Expected Entities

- `User.java` - User account, subscription info
- `Workspace.java` - Workspace/project grouping
- `Source.java` - Indexed source (folder, cloud drive, etc.)
- `Asset.java` - Core asset entity with metadata, embeddings
- `ExtractedEntity.java` - NER entities extracted from assets
- `Collection.java` - Smart collections (V2)
- `Relationship.java` - Asset relationships (V3)
- `SearchHistory.java` - Search query logging

## Conventions

- Use UUID for primary keys
- `@CreationTimestamp` / `@UpdateTimestamp` for audit fields
- Embeddings stored as `@Type(VectorType.class)` via pgvector
- JSONB fields via `@JdbcTypeCode(SqlTypes.JSON)`
- No business logic in entities — they are data containers
