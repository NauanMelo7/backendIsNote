CREATE table notes_document(
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_user(id),
    encrypted_payload BYTEA NOT NULL,
    content_nonce VARCHAR(250) NOT NULL,
    encryption_version VARCHAR(50) NOT NULL,
    share_visibility VARCHAR(50) NOT NULL
        CHECK (share_visibility IN ('PRIVATE', 'ANON_LINK', 'ACCOUNT_ONLY')),
    share_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notes_document_owner_id ON notes_document(owner_id);
CREATE INDEX idx_notes_document_deleted_at ON notes_document(deleted_at);
CREATE UNIQUE INDEX uq_notes_document_share_id
    ON notes_document(share_id)
    WHERE share_id IS NOT NULL;
