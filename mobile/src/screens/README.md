# Screens

Top-level screen/page components. Each screen corresponds to a route.

## Expected Screens

- `HomeScreen/` - Main dashboard, indexed asset count, quick search
- `SearchScreen/` - Natural language search interface
- `ResultsScreen/` - Search results display
- `AssetDetailScreen/` - Single asset view with metadata
- `SettingsScreen/` - App settings, indexed sources management
- `OnboardingScreen/` - First-run folder selection flow
- `SourcesScreen/` - Manage indexed sources

## Conventions

- Each screen is a folder with: `index.tsx`, `styles.ts`, optional `hooks.ts`
- Screens should be thin — delegate logic to hooks and services
- Navigation params defined in `types/navigation.ts`
