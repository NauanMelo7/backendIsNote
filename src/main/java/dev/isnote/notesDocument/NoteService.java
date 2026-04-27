package dev.isnote.notesDocument;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteResponseDTO createNote(User authenticatedUser, CreateNoteRequestDTO body) {

        Note note = new Note();
        note.setOwner(authenticatedUser);
        note.setEncryptedPayload(body.encryptedPayload());
        note.setContentNonce(body.contentNonce());
        note.setEncryptionVersion((body.encryptionVersion()));
        note.setShareVisibility((body.shareVisibility()));

        if(body.shareVisibility() == NoteShareVisibility.ANON_LINK){
            note.setShareId(UUID.randomUUID());
        }

        Note saved = this.noteRepository.save(note);

        return mapResponse(saved);
    }

    public NoteResponseDTO updateNote(User authenticatedUser, UpdateNoteRequestDTO body, UUID noteId) {

        Note note = this.noteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));


        note.setOwner(authenticatedUser);
        note.setEncryptedPayload(body.encryptedPayload());
        note.setContentNonce(body.contentNonce());
        note.setEncryptionVersion(body.encryptionVersion());
        note.setShareVisibility(body.shareVisibility());
        note.setVersion(body.version());

        if(body.shareVisibility() == NoteShareVisibility.ANON_LINK){
            note.setShareId(UUID.randomUUID());
        }

        Note saved = this.noteRepository.save(note);

        return mapResponse(saved);

    }

    public NoteResponseDTO mapResponse(Note note) {
       return new NoteResponseDTO(
            note.getId(),
            note.getShareVisibility(),
            note.getShareId(),
            note.getCreatedAt(),
            note.getUpdatedAt(),
            note.getVersion()
        );

    }

}
