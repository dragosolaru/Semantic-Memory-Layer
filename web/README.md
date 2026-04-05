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

## Database

The backend uses an H2 file-based database by default. See [backend README](../backend/README.md) for details.

To access the database:
- **H2 Console**: `http://localhost:8080/h2-console` (enable with `spring.h2.console.enabled=true`)
- **pgAdmin4**: Connect to PostgreSQL if configured (see backend README)

## Tech Stack

- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: CSS Modules / Global CSS
- **State**: React Context for auth
- **API**: REST with JWT Bearer tokens