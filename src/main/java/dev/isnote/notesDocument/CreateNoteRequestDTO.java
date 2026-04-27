package dev.isnote.notesDocument;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNoteRequestDTO(
    @NotNull
    byte[] encryptedPayload,
    @NotBlank
    String contentNonce,
    @NotBlank
    String encryptionVersion,
    @NotNull
    NoteShareVisibility shareVisibility
) {
}
