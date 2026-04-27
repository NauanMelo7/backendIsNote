CREATE TABLE app_user (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name   VARCHAR(100) NOT NULL,
    avatar_storage_key VARCHAR(255) NULL,
    role VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ NULL
);

CREATE INDEX idx_app_user_email ON app_user (email);
