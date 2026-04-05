# Tests

Unit and integration tests for the mobile app.

## Structure

Mirror the `src/` structure:
- `components/Button.test.tsx`
- `services/searchService.test.ts`
- `hooks/useSearch.test.tsx`

## Conventions

- Use Jest + React Native Testing Library
- Test files co-located with source or mirrored here
- Name: `*.test.tsx` for component tests, `*.test.ts` for logic

## Commands

```bash
npm test                    # Run all tests
npm test -- --coverage      # With coverage
npm test -- --watch         # Watch mode
```
