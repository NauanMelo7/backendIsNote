package dev.isnote.diagramsDocument;

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
public class DiagramService {

    private final DiagramRepository diagramRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public DiagramService(
        DiagramRepository diagramRepository,
        UserRepository userRepository,
        AssignmentRepository assignmentRepository
    ) {
        this.diagramRepository = diagramRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public DiagramDocumentResponseDTO createDiagram(User authenticatedUser, CreateDiagramRequestDTO body) {
        User owner = this.userRepository.findById(authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        Diagram diagram = new Diagram();
        diagram.setOwner(owner);
        diagram.setEncryptedPayload(body.encryptedPayload());
        diagram.setContentNonce(body.contentNonce());
        diagram.setEncryptionVersion(body.encryptionVersion());
        diagram.setShareVisibility(body.shareVisibility());

        if (body.shareVisibility() == DiagramShareVisibility.ANON_LINK) {
            diagram.setShareId(UUID.randomUUID());
        }

        Diagram saved = this.diagramRepository.save(diagram);
        return mapDiagramDocumentResponse(saved);
    }

    public DiagramDocumentResponseDTO updateDiagram(
        User authenticatedUser,
        UpdateDiagramRequestDTO body,
        UUID diagramId
    ) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found", HttpStatus.NOT_FOUND));

        if (!diagram.getVersion().equals(body.version())) {
            throw new BusinessException("Diagram was updated by another user", HttpStatus.CONFLICT);
        }

        diagram.setEncryptedPayload(body.encryptedPayload());
        diagram.setContentNonce(body.contentNonce());
        diagram.setEncryptionVersion(body.encryptionVersion());
        diagram.setShareVisibility(body.shareVisibility());

        if (body.shareVisibility() == DiagramShareVisibility.ANON_LINK && diagram.getShareId() == null) {
            diagram.setShareId(UUID.randomUUID());
        }

        try {
            return mapDiagramDocumentResponse(this.diagramRepository.save(diagram));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException("Diagram was updated by another user", HttpStatus.CONFLICT);
        }
    }

    public List<DiagramDocumentResponseDTO> findAllDiagram(User authenticatedUser) {
        List<Diagram> diagrams = this.diagramRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(authenticatedUser.getId());
        return mapListResponse(diagrams);
    }

    public DiagramDocumentResponseDTO findDiagramDocument(User authenticatedUser, UUID diagramId) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found", HttpStatus.NOT_FOUND));

        return mapDiagramDocumentResponse(diagram);
    }

    public void trashDiagram(User authenticatedUser, UUID diagramId) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found", HttpStatus.NOT_FOUND));

        diagram.setDeletedAt(Instant.now());
        this.diagramRepository.save(diagram);
    }

    public List<DiagramDocumentResponseDTO> listAllTrash(User authenticatedUser) {
        List<Diagram> diagrams = this.diagramRepository.findAllByOwnerIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(authenticatedUser.getId());
        return mapListResponse(diagrams);
    }

    public DiagramDocumentResponseDTO restoreDiagramTrash(User authenticatedUser, UUID diagramId) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found", HttpStatus.NOT_FOUND));

        diagram.setDeletedAt(null);
        Diagram restored = this.diagramRepository.save(diagram);
        return mapDiagramDocumentResponse(restored);
    }

    public void deleteDiagram(User authenticatedUser, UUID diagramId) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNotNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found or not in trash", HttpStatus.NOT_FOUND));

        this.assignmentRepository.deleteByOwner_IdAndItemTypeAndItemId(
            authenticatedUser.getId(),
            ItemType.DIAGRAM,
            diagramId
        );

        this.diagramRepository.delete(diagram);
    }

    public DiagramDocumentResponseDTO updateShare(
        User authenticatedUser,
        UUID diagramId,
        UpdateDiagramShareRequestDTO shareVisibility
    ) {
        Diagram diagram = this.diagramRepository.findByIdAndOwnerIdAndDeletedAtIsNull(diagramId, authenticatedUser.getId())
            .orElseThrow(() -> new BusinessException("Diagram not found or in trash", HttpStatus.NOT_FOUND));

        diagram.setShareVisibility(shareVisibility.shareVisibility());
        if (shareVisibility.shareVisibility() == DiagramShareVisibility.ANON_LINK && diagram.getShareId() == null) {
            diagram.setShareId(UUID.randomUUID());
        }

        this.diagramRepository.save(diagram);
        return mapDiagramDocumentResponse(diagram);
    }

    public DiagramDocumentResponseDTO mapDiagramDocumentResponse(Diagram diagram) {
        return new DiagramDocumentResponseDTO(
            diagram.getId(),
            diagram.getEncryptedPayload(),
            diagram.getContentNonce(),
            diagram.getEncryptionVersion(),
            diagram.getShareVisibility(),
            diagram.getShareId(),
            diagram.getCreatedAt(),
            diagram.getUpdatedAt(),
            diagram.getDeletedAt(),
            diagram.getVersion()
        );
    }

    public List<DiagramDocumentResponseDTO> mapListResponse(List<Diagram> diagrams) {
        return diagrams.stream()
            .map(this::mapDiagramDocumentResponse)
            .toList();
    }
}
