package dev.isnote.notesDocument;

import java.time.Instant;
import java.util.UUID;

public record NoteResponseDTO(
    UUID id,
    NoteShareVisibility noteShareVisibility,
    UUID sharedId,
    Instant createdAt,
    Instant updatedAt,
    long version
) {
}
