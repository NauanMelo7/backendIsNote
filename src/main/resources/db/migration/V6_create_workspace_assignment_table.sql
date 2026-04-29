CREATE TABLE workspace_assignment (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_user(id),
    folder_id UUID NOT NULL REFERENCES workspace_folder(id),
    item_type VARCHAR(250) NOT NULL,
    item_id UUID NOT NULL,
    position BIGINT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    UNIQUE (owner_id, item_type, item_id)
);

CREATE INDEX idx_folder_and_owner ON workspace_assignment(owner_id, folder_id);
CREATE INDEX idx_owner_item_type_item_id ON workspace_assignment(owner_id, item_type, item_id);

