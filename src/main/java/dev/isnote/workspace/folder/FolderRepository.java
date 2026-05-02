package dev.isnote.workspace.folder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    Optional<Folder> findByIdAndOwner_IdAndDeletedAtIsNull(UUID id, UUID ownerId);}
