package dev.isnote.notesDocument;

import jakarta.validation.constraints.NotNull;

public record UpdateShareRequestDTO(
    @NotNull(message = "Share visibility is required")
    NoteShareVisibility shareVisibility
) {
}
