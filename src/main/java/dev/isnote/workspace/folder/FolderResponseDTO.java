package dev.isnote.workspace.folder;


import jakarta.persistence.Column;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;


public record FolderResponseDTO(
    UUID id,
    UUID userId,
    UUID folderId,
    byte[] encryptedPayload,
    String contentNonce,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt,
    Integer version
) {
}
