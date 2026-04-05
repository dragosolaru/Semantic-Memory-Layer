# Shared Types & Schemas

Cross-layer type definitions and schemas shared between mobile, web, and backend.

## Structure

```
shared/
├── types/        # TypeScript type definitions
├── schemas/      # JSON Schema / Zod schemas for validation
└── constants/    # Shared constants
```

## Purpose

- Single source of truth for types used across all layers
- Prevents type drift between frontend and backend
- API contracts defined here first, then implemented

## Types (TypeScript)

- `asset.ts` - Asset entity, metadata types
- `user.ts` - User, subscription types
- `search.ts` - Search query, result types
- `source.ts` - Source entity types
- `api.ts` - API response wrappers, pagination

## Schemas

- Validation schemas that match backend DTOs
- Used by frontend for request validation
- Used by backend for request validation (generate from Java annotations)

## Constants

- File type mappings
- Max file sizes
- Default indexing settings
- API endpoint paths

## Conventions

- Types are the contract — backend and frontend must conform
- When backend changes, update shared types first
- Use code generation where possible (OpenAPI spec → TypeScript types)
