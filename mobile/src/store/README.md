# Store

State management layer.

## Recommended: Zustand

Lightweight, simple, no boilerplate.

## Expected Stores

- `authStore.ts` - User session, token, auth state
- `assetStore.ts` - Indexed assets cache, sync status
- `searchStore.ts` - Search history, recent queries
- `settingsStore.ts` - User preferences, indexed sources config
- `indexingStore.ts` - Indexing progress, status, errors

## Conventions

- One store per domain
- Persist only what's needed (auth token, settings)
- Don't persist large data (asset lists come from API)
