package dev.isnote.workspace.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    List<Assignment> findAllByOwner_IdOrderByCreatedAtAsc(UUID ownerId);

    Optional<Assignment> findByOwner_IdAndItemTypeAndItemId(UUID ownerId, ItemType itemType, UUID itemId);

    void deleteByOwner_IdAndItemTypeAndItemId(UUID ownerId, ItemType itemType, UUID itemId);

    void deleteAllByOwner_IdAndFolder_IdIn(UUID ownerId, List<UUID> folderIds);
}
