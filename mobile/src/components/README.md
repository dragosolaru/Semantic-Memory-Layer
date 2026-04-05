# Reusable UI Components

Shared, reusable components used across screens.

## Conventions

- Each component gets its own folder: `ComponentName/index.tsx`
- Co-locate styles: `ComponentName/styles.ts`
- Co-locate tests: `ComponentName/__tests__/ComponentName.test.tsx`
- Export from barrel file: `index.ts`

## Common Components

- `Button/` - Primary, secondary, ghost variants
- `Input/` - Text inputs, search bars
- `Card/` - Asset preview cards
- `AssetThumbnail/` - Image/document thumbnails
- `ResultList/` - Search results container
- `EmptyState/` - No results placeholder
- `LoadingSpinner/` - Loading indicator
- `Header/` - Screen headers
- `BottomSheet/` - Modal bottom sheets
