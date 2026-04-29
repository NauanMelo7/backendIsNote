create table user_content_key (
    id uuid primary key,
    user_id uuid not null unique references app_user(id) on delete cascade,
    wrapped_key bytea not null,
    wrapping_salt bytea not null,
    wrapping_nonce bytea not null,
    kdf_algorithm varchar(50) not null,
    kdf_memory_cost_kib integer not null,
    kdf_iterations integer not null,
    kdf_parallelism integer not null,
    content_encryption_version varchar(50) not null,
    version bigint not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_user_content_key_user_id on user_content_key(user_id);
