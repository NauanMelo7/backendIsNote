package dev.isnote.workspace.assignment;

import java.time.Instant;
import java.util.UUID;

public record AssignmentResponseDTO(
    UUID id,
    UUID ownerId,
    UUID folderId,
    ItemType itemType,
    UUID itemId,
    Long position,
    Instant createdAt,
    Instant updatedAt,
    Integer version
) {
}
