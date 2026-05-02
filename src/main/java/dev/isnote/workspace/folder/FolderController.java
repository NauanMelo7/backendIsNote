package dev.isnote.workspace.folder;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class FolderController {

    private FolderService folderService;
    public FolderController(FolderService folderService){
        this.folderService = folderService;
    }

    @PostMapping("/folder")
    public ResponseEntity<FolderResponseDTO> createFolder(@AuthenticationPrincipal User user, @RequestBody FolderRequestDTO body) {
        FolderResponseDTO folder = this.folderService.createFolder(user, body);

       return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }
}
