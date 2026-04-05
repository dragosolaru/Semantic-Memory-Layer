# Database Migrations

Flyway migration files for PostgreSQL schema.

## Conventions

- Naming: `V{version}__{description}.sql`
- Example: `V001__create_users_table.sql`
- Each migration is idempotent
- Never modify existing migrations — create new ones
- Test migrations on a clean database before committing

## Migration Order

1. `V001__create_users_table.sql`
2. `V002__create_workspaces_table.sql`
3. `V003__create_sources_table.sql`
4. `V004__create_assets_table.sql`
5. `V005__create_entities_table.sql`
6. `V006__add_pgvector_extension.sql`
7. `V007__create_collections_table.sql` (V2)
8. `V008__create_relationships_table.sql` (V3)
