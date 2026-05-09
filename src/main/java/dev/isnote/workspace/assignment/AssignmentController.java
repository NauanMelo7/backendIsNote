package dev.isnote.workspace.assignment;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspace")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentResponseDTO>> listAssignments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(assignmentService.listAssignments(user));
    }

    @PutMapping("/items/{itemType}/{itemId}/assignment")
    public ResponseEntity<AssignmentResponseDTO> upsertAssignment(
        @AuthenticationPrincipal User user,
        @PathVariable ItemType itemType,
        @PathVariable UUID itemId,
        @Valid @RequestBody UpsertAssignmentRequestDTO body
    ) {
        return ResponseEntity.ok(assignmentService.upsertAssignment(user, itemType, itemId, body));
    }

    @DeleteMapping("/items/{itemType}/{itemId}/assignment")
    public ResponseEntity<Void> removeAssignment(
        @AuthenticationPrincipal User user,
        @PathVariable ItemType itemType,
        @PathVariable UUID itemId
    ) {
        assignmentService.removeAssignment(user, itemType, itemId);
        return ResponseEntity.noContent().build();
    }
}
