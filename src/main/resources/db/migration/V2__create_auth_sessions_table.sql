CREATE TABLE auth_session (
    id                 UUID PRIMARY KEY,
    user_id            UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    refresh_token_hash VARCHAR(255) NOT NULL,
    expires_at         TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_used_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    revoked_at         TIMESTAMPTZ NULL
);

CREATE INDEX idx_auth_session_user_id ON auth_session (user_id);
CREATE INDEX idx_auth_session_expires_at ON auth_session (expires_at);
