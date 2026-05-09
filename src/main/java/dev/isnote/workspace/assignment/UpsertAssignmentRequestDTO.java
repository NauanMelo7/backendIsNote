package dev.isnote.workspace.assignment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpsertAssignmentRequestDTO(
    @NotNull UUID folderId,
    Long position
) {
}
