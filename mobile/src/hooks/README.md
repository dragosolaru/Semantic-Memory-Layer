# Custom Hooks

Reusable React hooks.

## Expected Hooks

- `useAuth.ts` - Auth state and actions
- `useSearch.ts` - Search query execution and results
- `useAssets.ts` - Asset loading, pagination
- `useIndexing.ts` - Indexing progress tracking
- `useDebounce.ts` - Debounce utility for search input
- `usePermissions.ts` - Photo library / file system permissions

## Conventions

- Hooks start with `use` prefix
- Return object with named values
- Keep hooks focused — don't combine unrelated logic
