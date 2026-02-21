-- V008: Transaction Tags
-- Tag types and tags for user-defined transaction categorization

CREATE TABLE tag_types (
    id UUID PRIMARY KEY,
    row_version BIGINT NOT NULL DEFAULT 0,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    row_version BIGINT NOT NULL DEFAULT 0,
    id_tag_type UUID NOT NULL REFERENCES tag_types(id),
    code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT uq_tag_code_per_type UNIQUE (id_tag_type, code)
);

CREATE TABLE transaction_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_transaction UUID NOT NULL REFERENCES transactions(id),
    id_tag UUID NOT NULL REFERENCES tags(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_transaction_tag UNIQUE (id_transaction, id_tag)
);

CREATE INDEX idx_tags_tag_type ON tags(id_tag_type);
CREATE INDEX idx_transaction_tags_transaction ON transaction_tags(id_transaction);
CREATE INDEX idx_transaction_tags_tag ON transaction_tags(id_tag);
