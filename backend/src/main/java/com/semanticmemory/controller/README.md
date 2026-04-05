# REST Controllers

API endpoint definitions.

## Implemented Controllers

- `AuthController.java` - Login, register, change password

## Expected Controllers

- `AssetController.java` - Asset CRUD, list, detail
- `SearchController.java` - Search queries, results
- `SourceController.java` - Source management (add, remove, status)
- `UserController.java` - User profile, settings
- `IndexingController.java` - Indexing status, trigger re-index

## Conventions

- Controllers are thin — delegate to services
- Return DTOs, never entities
- Use `@Valid` for request validation
- Consistent REST conventions (plural nouns, proper status codes)
