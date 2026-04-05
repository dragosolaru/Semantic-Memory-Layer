# Scripts

Automation scripts for development and CI.

## Structure

```
scripts/
├── dev/     # Local development helpers
└── ci/      # CI/CD pipeline scripts
```

## Dev Scripts

### start.sh

Script principal pentru pornirea serviciilor de dezvoltare.

**Locație:** `scripts/dev/start.sh`

**Utilizare:**

```bash
# Pornește toate serviciile (backend + web + mobile)
./scripts/dev/start.sh all

# Pornește doar backend (Spring Boot)
./scripts/dev/start.sh backend

# Pornește doar web (Next.js)
./scripts/dev/start.sh web

# Pornește doar mobile (React Native)
./scripts/dev/start.sh mobile

# Oprește toate serviciile
./scripts/dev/start.sh stop
```

**Porturi:**
- Backend: `http://localhost:8080`
- Web: `http://localhost:3000`
- Mobile: `http://localhost:8081`

**Cerințe:**
- Maven (`mvn` sau `mvnw` din directorul backend)
- Node.js + npm (pentru web și mobile)

---

## Expected Dev Scripts

- `dev/setup.sh` - One-command local environment setup
- `dev/seed-db.sh` - Seed database with test data
- `dev/reset.sh` - Clean and rebuild everything

## Expected CI Scripts

- `ci/lint.sh` - Run linters across all projects
- `ci/test.sh` - Run test suites
- `ci/build.sh` - Build all artifacts
- `ci/deploy.sh` - Deployment script

## Conventions

- All scripts are executable (`chmod +x`)
- Use bash, POSIX-compatible where possible
- Exit on error (`set -e`)
- Print what you're doing (echo before actions)
