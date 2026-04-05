# Scripts

Automation scripts for development and CI.

## Structure

```
scripts/
├── dev/     # Local development helpers
└── ci/      # CI/CD pipeline scripts
```

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
