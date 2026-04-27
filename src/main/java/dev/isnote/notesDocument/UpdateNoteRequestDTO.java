package dev.isnote.notesDocument;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateNoteRequestDTO(
    @NotNull
    byte[] encryptedPayload,
    @NotBlank
    String contentNonce,
    @NotBlank
    String encryptionVersion,
    @NotNull
    NoteShareVisibility shareVisibility,
    @NotNull
    int version

) {
}
