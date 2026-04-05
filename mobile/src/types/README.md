# TypeScript Types

Shared type definitions for the mobile app.

## Expected Types

- `asset.ts` - Asset entity, asset metadata, asset type enums
- `user.ts` - User profile, subscription tier
- `search.ts` - Search query, search result, ranking info
- `source.ts` - Source entity, source type enum, source status
- `navigation.ts` - Route param lists for all screens
- `api.ts` - API request/response wrappers, paginated response
- `indexing.ts` - Indexing status, progress info

## Conventions

- Mirror backend entity types where applicable
- Use `shared/types/` as source of truth for cross-layer types
- Re-export from `shared/types/` in local files for convenience
- All types are exported
