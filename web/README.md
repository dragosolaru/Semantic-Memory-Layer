# Semantic Memory Layer - Web Application

Next.js web application for the Semantic Memory Layer platform.

## Features

- **Authentication**
  - User registration
  - User login
  - JWT-based session management
  - Password change

- **Dashboard**
  - Welcome page with user info
  - Quick access to search, upload, and statistics

- **Search**
  - Semantic search across your memory
  - Results with relevance scores
  - File type and metadata display

- **Settings**
  - Change password

## Pages

| Route | Description |
|-------|-------------|
| `/` | Root - redirects to login or home |
| `/login` | User login |
| `/register` | User registration |
| `/home` | Dashboard (requires auth) |
| `/search` | Semantic search (requires auth) |
| `/change-password` | Change password (requires auth) |

## Project Structure

```
web/
├── app/                    # Next.js App Router pages
│   ├── login/page.tsx      # Login page
│   ├── register/page.tsx   # Registration page
│   ├── home/page.tsx       # Dashboard
│   ├── search/page.tsx     # Search page
│   ├── change-password/    # Password change
│   ├── ProtectedRoute.tsx  # Auth guard component
│   ├── layout.tsx         # Root layout
│   └── page.tsx          # Root redirect
├── lib/                   # Core utilities
│   ├── api.ts           # API client (in-memory token)
│   ├── auth.tsx         # Authentication context
│   └── types.ts        # TypeScript types
├── components/
│   └── Navbar.tsx      # Navigation bar
└── public/              # Static assets
```

## Setup

```bash
cd web
npm install
npm run dev
```

The app runs on `http://localhost:3000`

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080/api` | Backend API URL |

## Security

This application implements security best practices:

### Token Storage
- **JWT tokens are stored in memory only** - NOT in localStorage or sessionStorage
- This prevents XSS attacks from stealing tokens
- User must re-authenticate after page refresh (token is cleared from memory)
- User data (non-sensitive) is cached in localStorage for faster initial load

### Authentication Flow
1. User logs in with credentials
2. Server returns JWT token + user data
3. Token is stored in React state (memory) via `api.setToken()`
4. User data is cached in localStorage for faster subsequent page loads
5. On logout or token expiration, token is cleared from memory

### Protected Routes
All authenticated pages use the `ProtectedRoute` component which:
- Checks if user is authenticated
- Redirects to login if not authenticated
- Shows loading state while checking auth

### API Security
- All authenticated API calls include JWT in Authorization header
- Tokens are retrieved from in-memory store (not localStorage)
- Logout endpoint invalidates token on server

### Known Limitations
- User must log in again after closing browser tab (token not persisted)
- This is by design for security - trade-off between convenience and safety

## Code Review Summary

### Resolved Issues

| Issue | Status |
|------|--------|
| JWT in localStorage (XSS vulnerability) | ✅ Fixed - In-memory token |
| Duplicate auth check logic | ✅ Fixed - ProtectedRoute component |
| Raw fetch in change-password | ✅ Fixed - API helper |
| No logout API call | ✅ Fixed - Async logout |
| Hardcoded API URL | ✅ Fixed - API_URL export |
| Unused imports | ✅ Fixed |

### Architecture

```
Local Storage:
  - user: { id, email, name, subscriptionTier? }  (cached for UX)
  - NO token stored (security)

In-Memory (api.ts):
  - currentToken: string | null  (JWT, not persisted)

Auth Flow:
  login() → setApiToken() → localStorage.setItem('user')
  api calls → getToken() → Authorization header
  logout() → api.logout() → clear memory + localStorage
```

## Tech Stack

- **Framework**: Next.js 16.2.2 (App Router)
- **Language**: TypeScript
- **Styling**: CSS Modules / Global CSS
- **State**: React Context for auth
- **API**: REST with JWT Bearer tokens
- **Security**: In-memory token storage

## Database

The backend uses PostgreSQL as the default database. See [backend README](../backend/README.md) for details.

To access the database:
- **pgAdmin4**: `/Library/PostgreSQL/18/pgAdmin 4.app`
  - Host: `localhost`, Port: `5432`
  - Database: `semanticmemory`
  - User: `postgres`, Password: `postgres`