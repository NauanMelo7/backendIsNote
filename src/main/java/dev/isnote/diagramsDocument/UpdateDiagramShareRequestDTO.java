package dev.isnote.diagramsDocument;

import jakarta.validation.constraints.NotNull;

public record UpdateDiagramShareRequestDTO(
    @NotNull
    DiagramShareVisibility shareVisibility
) {
}
