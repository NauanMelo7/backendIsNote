package dev.isnote.diagramsDocument;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiagramRepository extends JpaRepository<Diagram, UUID> {

    List<Diagram> findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(UUID ownerId);

    List<Diagram> findAllByOwnerIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(UUID ownerId);

    Optional<Diagram> findByIdAndOwnerIdAndDeletedAtIsNull(UUID diagramId, UUID ownerId);

    Optional<Diagram> findByIdAndOwnerIdAndDeletedAtIsNotNull(UUID diagramId, UUID ownerId);
}
