CREATE TABLE diagrams_document (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    encrypted_payload BYTEA NOT NULL,
    content_nonce VARCHAR(250) NOT NULL,
    encryption_version VARCHAR(50) NOT NULL,
    share_visibility VARCHAR(50) NOT NULL CHECK (share_visibility IN ('PRIVATE', 'ANON_LINK', 'ACCOUNT_ONLY')),
    share_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_diagrams_document_owner_updated_at
    ON diagrams_document (owner_id, updated_at DESC)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_diagrams_document_owner_deleted_at
    ON diagrams_document (owner_id, deleted_at DESC)
    WHERE deleted_at IS NOT NULL;
