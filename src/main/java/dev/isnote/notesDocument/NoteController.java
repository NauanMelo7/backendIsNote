package dev.isnote.notesDocument;

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

        return ResponseEntity.status(HttpStatus.OK).body(note);
    }

    @GetMapping("/notes")
    public ResponseEntity<List<NoteResponseDTO>> findAllNotes(@AuthenticationPrincipal User user) {
         List<NoteResponseDTO> findAllNotes = this.noteService.findAllNote(user);

         return ResponseEntity.status(HttpStatus.OK).body(findAllNotes);
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<NoteDocumentResponseDTO> findNote(@AuthenticationPrincipal User user, @PathVariable UUID id){
        NoteDocumentResponseDTO note = this.noteService.finNoteDocumentNote(user, id);

        return ResponseEntity.status(HttpStatus.OK).body(note);
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity trashNote(@AuthenticationPrincipal User user, @PathVariable UUID id){
        this.noteService.trashNote(user, id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/notes/trash")
    public ResponseEntity listAllTrashNotes(@AuthenticationPrincipal User user){

        List<NoteResponseDTO> notes = this.noteService.listAllTrash(user);

        return ResponseEntity.status(HttpStatus.OK).body(notes);
    }

    @PostMapping("/notes/{id}/restore")
    public ResponseEntity<NoteResponseDTO> restoreNote(@AuthenticationPrincipal User user, @PathVariable UUID id){
        NoteResponseDTO noteResponseDTO = this.noteService.restaureNoteTrash(user, id);

        return ResponseEntity.status(HttpStatus.OK).body(noteResponseDTO);
    }

    @DeleteMapping("/notes/{id}/permanent")
    public ResponseEntity deleteNote(@AuthenticationPrincipal User user, @PathVariable UUID id){
        this.noteService.deletNote(user, id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
