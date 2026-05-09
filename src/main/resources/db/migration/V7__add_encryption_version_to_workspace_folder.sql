ALTER TABLE workspace_folder
ADD COLUMN encryption_version VARCHAR(100) NOT NULL DEFAULT 'aes-gcm:v1';
