package dev.isnote.diagramsDocument;

import java.time.Instant;
import java.util.UUID;

public record DiagramDocumentResponseDTO(
    UUID id,
    byte[] encryptedPayload,
    String contentNonce,
    String encryptionVersion,
    DiagramShareVisibility shareVisibility,
    UUID shareId,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt,
    Integer version
) {
}
