# Services

External service integrations and API clients.

## Expected Services

- `api.ts` - Base API client (axios/fetch wrapper)
- `assetService.ts` - Asset CRUD operations
- `searchService.ts` - Search query execution
- `indexingService.ts` - Local file scanning and metadata extraction
- `authService.ts` - Authentication flows
- `storageService.ts` - Local storage (AsyncStorage/MMKV)
- `photoLibraryService.ts` - iOS/Android photo library access
- `fileSystemService.ts` - Local file system operations
- `thumbnailService.ts` - Thumbnail generation

## Conventions

- Each service is a single responsibility module
- No UI logic in services
- All services are injectable/testable
- API services return typed responses from `shared/types/`
