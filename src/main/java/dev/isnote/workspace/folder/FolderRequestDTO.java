package dev.isnote.workspace.folder;

import dev.isnote.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

public record FolderRequestDTO(
    UUID userId,
    UUID folderParentId,
    byte[] encryptedPayload,
    String contentNonce
) {
}
