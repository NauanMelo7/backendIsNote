package dev.isnote.notesDocument;

import java.time.Instant;
import java.util.UUID;

public record NoteDocumentResponseDTO(
    UUID id,
    byte[] encryptedPayload,
    String contentNonce,
    String encryptionVersion,
    NoteShareVisibility shareVisibility,
    UUID shareId,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt,
    Integer version
) {
}
