package dev.isnote.notesDocument;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc (UUID uuid);

    List<Note> findAllByOwnerIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(UUID id);

    Optional<Note> findByIdAndOwnerIdAndDeletedAtIsNull(UUID noteId, UUID ownerId);

    Optional<Note> findByIdAndOwnerId (UUID noteId, UUID ownerId);

    Optional<Note> findByIdAndOwnerIdAndDeletedAtIsNotNull (UUID noteId, UUID ownerId);

}
