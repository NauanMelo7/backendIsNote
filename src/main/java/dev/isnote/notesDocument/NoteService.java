package dev.isnote.notesDocument;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import dev.isnote.workspace.assignment.AssignmentRepository;
import dev.isnote.workspace.assignment.ItemType;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public NoteService(
        NoteRepository noteRepository,
        UserRepository userRepository,
        AssignmentRepository assignmentRepository
    ) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public NoteDocumentResponseDTO createNote(User authenticatedUser, CreateNoteRequestDTO body) {

        User findUser = this.userRepository.findById(authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        Note note = new Note();
        note.setOwner(findUser);
        note.setEncryptedPayload(body.encryptedPayload());
        note.setContentNonce(body.contentNonce());
        note.setEncryptionVersion((body.encryptionVersion()));
        note.setShareVisibility((body.shareVisibility()));

        if(body.shareVisibility() == NoteShareVisibility.ANON_LINK){
            note.setShareId(UUID.randomUUID());
        }

        Note saved = this.noteRepository.save(note);

        return mapNoteDocumentResponse(saved);
    }

    public NoteDocumentResponseDTO updateNote(User authenticatedUser, UpdateNoteRequestDTO body, UUID noteId) {

        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));

        if(!note.getVersion().equals(body.version())){
            throw new BusinessException("Note was updated by another user", HttpStatus.CONFLICT);
        }

        note.setEncryptedPayload(body.encryptedPayload());
        note.setContentNonce(body.contentNonce());
        note.setEncryptionVersion(body.encryptionVersion());
        note.setShareVisibility(body.shareVisibility());

        if(body.shareVisibility() == NoteShareVisibility.ANON_LINK && note.getShareId() == null){
            note.setShareId(UUID.randomUUID());
        }

        try {
            return mapNoteDocumentResponse(this.noteRepository.save(note));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException("Note was updated by another user", HttpStatus.CONFLICT);
        }

    }

    public List<NoteDocumentResponseDTO> findAllNote(User authenticatedUser) {
        List<Note> allNote = this.noteRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(authenticatedUser.getId());

        return mapListResponse(allNote);

    }

    public NoteDocumentResponseDTO finNoteDocumentNote(User authenticatedUser, UUID noteId){

        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note note found", HttpStatus.NOT_FOUND));

        return mapNoteDocumentResponse(note);

    }

    public void trashNote(User authenticatedUser, UUID noteId){
        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));

        note.setDeletedAt(Instant.now());
        this.noteRepository.save(note);
    }

    public List<NoteDocumentResponseDTO> listAllTrash(User authenticatedUser) {
        List<Note> findAllNote = this.noteRepository.findAllByOwnerIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(authenticatedUser.getId());

        return mapListResponse(findAllNote);
    }

    public NoteDocumentResponseDTO restaureNoteTrash(User authenticatedUser, UUID noteId){
        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found", HttpStatus.NOT_FOUND));

        note.setDeletedAt(null);

        Note restored = this.noteRepository.save(note);

        return mapNoteDocumentResponse(restored);
    }

    public void deleteNote (User authenticatedUser, UUID noteId){

        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(noteId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Note not found or not in trash", HttpStatus.NOT_FOUND));

        this.assignmentRepository.deleteByOwner_IdAndItemTypeAndItemId(
            authenticatedUser.getId(),
            ItemType.NOTE,
            noteId
        );
        this.noteRepository.delete(note);

    }

    public NoteDocumentResponseDTO updateShare(User authenticatedUser, UUID noteId, UpdateShareRequestDTO shareVisibility){

        Note note = this.noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(noteId, authenticatedUser.getId())
            .orElseThrow(() ->  new BusinessException("Note not found or in trash", HttpStatus.NOT_FOUND));

        note.setShareVisibility(shareVisibility.shareVisibility());
        if(shareVisibility.shareVisibility() == NoteShareVisibility.ANON_LINK && note.getShareId() == null) {
            note.setShareId(UUID.randomUUID());
        }

        this.noteRepository.save(note);

        return mapNoteDocumentResponse(note);

    }
    public NoteDocumentResponseDTO mapNoteDocumentResponse(Note note) {

        return new NoteDocumentResponseDTO(
            note.getId(),
            note.getEncryptedPayload(),
            note.getContentNonce(),
            note.getEncryptionVersion(),
            note.getShareVisibility(),
            note.getShareId(),
            note.getCreatedAt(),
            note.getUpdatedAt(),
            note.getDeletedAt(),
            note.getVersion()
        );
    }

    public List<NoteDocumentResponseDTO> mapListResponse(List<Note> notes){
        return notes.stream()
            .map(this::mapNoteDocumentResponse)
            .toList();
    }

}
