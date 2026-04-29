CREATE TABLE workspace_folder (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_user(id),
    parent_folder_id UUID NULL REFERENCES workspace_folder(id),
    encrypted_payload BYTEA NOT NULL,
    content_nonce VARCHAR(250) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ NULL,
    version BIGINT NOT NULL DEFAULT 0,

    check (id <> parent_folder_id)
);

CREATE INDEX idx_folder_owner ON workspace_folder(owner_id);
CREATE INDEX idx_folder_parent ON workspace_folder(owner_id, parent_folder_id);
CREATE INDEX idx_folder_deleted ON workspace_folder(owner_id) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_folder_not_deleted ON workspace_folder(owner_id) WHERE deleted_at IS NULL;
