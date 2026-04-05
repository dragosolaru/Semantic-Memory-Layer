# Configuration

Environment-specific and app-level configuration.

## Expected Files

- `env.ts` - Environment variable validation and typing
- `api.ts` - API base URL, timeouts, retry config
- `indexing.ts` - Indexing defaults (max file size, excluded extensions)
- `features.ts` - Feature flags

## Conventions

- Never hardcode secrets
- Use `.env` files (not committed)
- Validate env vars at startup, fail fast if missing
