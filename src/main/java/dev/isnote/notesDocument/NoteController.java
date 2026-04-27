package dev.isnote.notesDocument;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1")

public class NoteController {

    private final NoteService noteService;
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/notes")
    public ResponseEntity<NoteResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody CreateNoteRequestDTO createNote) {

        NoteResponseDTO newNote = this.noteService.createNote(user, createNote);

        return ResponseEntity.status(HttpStatus.CREATED).body(newNote);
    }

    @PatchMapping("/notes/{id}")
    public ResponseEntity<NoteResponseDTO> update(@PathVariable UUID id, @AuthenticationPrincipal User user, @RequestBody UpdateNoteRequestDTO updateNote) {
        NoteResponseDTO note = this.noteService.updateNote(user, updateNote, id);

        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }
}
