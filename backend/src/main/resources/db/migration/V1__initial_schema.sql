-- V1__create_users_table.sql
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    subscription_tier VARCHAR(20) DEFAULT 'FREE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- V2__create_workspaces_table.sql
CREATE TABLE IF NOT EXISTS workspaces (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V3__create_sources_table.sql
CREATE TABLE IF NOT EXISTS sources (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces(id),
    type VARCHAR(20),
    path_or_identifier VARCHAR(1024) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_indexed_at TIMESTAMP,
    asset_count INTEGER DEFAULT 0
);

-- V4__create_assets_table.sql
CREATE TABLE IF NOT EXISTS assets (
    id UUID PRIMARY KEY,
    source_id UUID NOT NULL REFERENCES sources(id),
    user_id UUID NOT NULL REFERENCES users(id),
    file_path VARCHAR(2048) NOT NULL,
    file_name VARCHAR(512) NOT NULL,
    file_type VARCHAR(20),
    mime_type VARCHAR(100),
    file_size_bytes BIGINT,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    indexed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    checksum VARCHAR(64),
    thumbnail_url VARCHAR(1024),
    metadata JSONB,
    extracted_text TEXT,
    entities JSONB,
    classification JSONB
);

CREATE INDEX IF NOT EXISTS idx_assets_user_id ON assets(user_id);
CREATE INDEX IF NOT EXISTS idx_assets_source_id ON assets(source_id);
CREATE INDEX IF NOT EXISTS idx_assets_status ON assets(status);
CREATE INDEX IF NOT EXISTS idx_assets_file_type ON assets(file_type);