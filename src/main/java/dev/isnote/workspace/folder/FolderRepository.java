package dev.isnote.workspace.folder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    Optional<Folder> findByIdAndOwner_IdAndDeletedAtIsNull(UUID id, UUID ownerId);

    Optional<Folder> findByIdAndOwner_IdAndDeletedAtIsNotNull(UUID id, UUID ownerId);

    List<Folder> findAllByOwner_IdOrderByCreatedAtAsc(UUID ownerId);

    List<Folder> findAllByOwner_IdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID ownerId);

    List<Folder> findAllByOwner_IdAndDeletedAtIsNotNullOrderByDeletedAtDesc(UUID ownerId);
}
