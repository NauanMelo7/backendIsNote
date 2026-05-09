package dev.isnote.diagramsDocument;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDiagramRequestDTO(
    @NotNull
    byte[] encryptedPayload,
    @NotBlank
    String contentNonce,
    @NotBlank
    String encryptionVersion,
    @NotNull
    DiagramShareVisibility shareVisibility
) {
}
