package dev.isnote.diagramsDocument;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DiagramController {

    private final DiagramService diagramService;

    public DiagramController(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @PostMapping("/diagrams")
    public ResponseEntity<DiagramDocumentResponseDTO> create(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody CreateDiagramRequestDTO createDiagram
    ) {
        DiagramDocumentResponseDTO newDiagram = this.diagramService.createDiagram(user, createDiagram);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDiagram);
    }

    @PatchMapping("/diagrams/{id}")
    public ResponseEntity<DiagramDocumentResponseDTO> update(
        @PathVariable UUID id,
        @AuthenticationPrincipal User user,
        @Valid @RequestBody UpdateDiagramRequestDTO updateDiagram
    ) {
        DiagramDocumentResponseDTO diagram = this.diagramService.updateDiagram(user, updateDiagram, id);
        return ResponseEntity.status(HttpStatus.OK).body(diagram);
    }

    @GetMapping("/diagrams")
    public ResponseEntity<List<DiagramDocumentResponseDTO>> findAllDiagrams(@AuthenticationPrincipal User user) {
        List<DiagramDocumentResponseDTO> diagrams = this.diagramService.findAllDiagram(user);
        return ResponseEntity.status(HttpStatus.OK).body(diagrams);
    }

    @GetMapping("/diagrams/{id}")
    public ResponseEntity<DiagramDocumentResponseDTO> findDiagram(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        DiagramDocumentResponseDTO diagram = this.diagramService.findDiagramDocument(user, id);
        return ResponseEntity.status(HttpStatus.OK).body(diagram);
    }

    @DeleteMapping("/diagrams/{id}")
    public ResponseEntity<Void> trashDiagram(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        this.diagramService.trashDiagram(user, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/diagrams/trash")
    public ResponseEntity<List<DiagramDocumentResponseDTO>> listAllTrashDiagrams(@AuthenticationPrincipal User user) {
        List<DiagramDocumentResponseDTO> diagrams = this.diagramService.listAllTrash(user);
        return ResponseEntity.status(HttpStatus.OK).body(diagrams);
    }

    @PostMapping("/diagrams/{id}/restore")
    public ResponseEntity<DiagramDocumentResponseDTO> restoreDiagram(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        DiagramDocumentResponseDTO diagram = this.diagramService.restoreDiagramTrash(user, id);
        return ResponseEntity.status(HttpStatus.OK).body(diagram);
    }

    @DeleteMapping("/diagrams/{id}/permanent")
    public ResponseEntity<Void> deleteDiagram(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        this.diagramService.deleteDiagram(user, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/diagrams/{id}/share")
    public ResponseEntity<DiagramDocumentResponseDTO> updateShare(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateDiagramShareRequestDTO shareVisibility
    ) {
        DiagramDocumentResponseDTO diagram = this.diagramService.updateShare(user, id, shareVisibility);
        return ResponseEntity.status(HttpStatus.OK).body(diagram);
    }
}
