package dev.isnote.notesDocument;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    public List<NoteResponseDTO> findAllNote(User authenticatedUser) {
        List<Note> allNote = this.noteRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(authenticatedUser.getId());

        return mapListResponse(allNote);

    }

    public NoteDocumentResponseDTO finNoteDocumentNote(User authenticatedUser, UUID noteId){

        Note note = this.noteRepository.findByIdAndOwnerId(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note note found", HttpStatus.NOT_FOUND));

        return new NoteDocumentResponseDTO(
            note.getId(),
            note.getEncryptedPayload(),
            note.getContentNonce(),
            note.getEncryptionVersion(),
            note.getShareVisibility(),
            note.getShareId(),
            note.getCreatedAt(),
            note.getUpdatedAt(),
            note.getVersion()
        );

    }

    public void trashNote(User authenticatedUser, UUID noteId){
        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));

        note.setDeletedAt(Instant.now());
        this.noteRepository.save(note);
    }

    public List<NoteResponseDTO> listAllTrash(User authenticatedUser) {
        List<Note> findAllNote = this.noteRepository.findAllByOwnerIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(authenticatedUser.getId());

        return mapListResponse(findAllNote);


    }

    public NoteResponseDTO restaureNoteTrash(User authenticatedUser, UUID noteId){
        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));

        note.setDeletedAt(null);

        this.noteRepository.save(note);

        return mapResponse(note);
    }

    public void deletNote (User authenticatedUser, UUID noteId){

        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found or not in trash", HttpStatus.NOT_FOUND));

        this.noteRepository.delete(note);

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

    public List<NoteResponseDTO> mapListResponse(List<Note> notes){

        List<NoteResponseDTO> listNoteDTO = new ArrayList<>();

        for(Note listNote : notes) {

            NoteResponseDTO noteResponse = new NoteResponseDTO(
                listNote.getId(),
                listNote.getShareVisibility(),
                listNote.getShareId(),
                listNote.getCreatedAt(),
                listNote.getUpdatedAt(),
                listNote.getVersion()
            );

            listNoteDTO.add(noteResponse);
        }

        return listNoteDTO;
    }

}
