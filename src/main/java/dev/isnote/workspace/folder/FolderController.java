package dev.isnote.workspace.folder;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspace/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<FolderResponseDTO> createFolder(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody CreateFolderRequestDTO body
    ) {
        FolderResponseDTO folder = folderService.createFolder(user, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }

    @GetMapping
    public ResponseEntity<List<FolderResponseDTO>> listFolders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(folderService.listActiveFolders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderResponseDTO> getFolder(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(folderService.getFolder(user, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FolderResponseDTO> updateFolder(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateFolderRequestDTO body
    ) {
        return ResponseEntity.ok(folderService.updateFolder(user, id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> trashFolder(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        folderService.trashFolder(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trash")
    public ResponseEntity<List<FolderResponseDTO>> listTrashFolders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(folderService.listTrashFolders(user));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<FolderResponseDTO> restoreFolder(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(folderService.restoreFolder(user, id));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deleteFolderPermanently(
        @AuthenticationPrincipal User user,
        @PathVariable UUID id
    ) {
        folderService.deleteFolderPermanently(user, id);
        return ResponseEntity.noContent().build();
    }
}
