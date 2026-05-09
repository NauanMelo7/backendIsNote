package dev.isnote.workspace.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateFolderRequestDTO(
    UUID parentFolderId,
    @NotNull byte[] encryptedPayload,
    @NotBlank String contentNonce,
    @NotBlank String encryptionVersion
) {
}
