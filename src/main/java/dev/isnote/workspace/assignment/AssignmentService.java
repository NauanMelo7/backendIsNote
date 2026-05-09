package dev.isnote.workspace.assignment;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.workspace.folder.Folder;
import dev.isnote.workspace.folder.FolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final FolderRepository folderRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, FolderRepository folderRepository) {
        this.assignmentRepository = assignmentRepository;
        this.folderRepository = folderRepository;
    }

    public List<AssignmentResponseDTO> listAssignments(User user) {
        return assignmentRepository.findAllByOwner_IdOrderByCreatedAtAsc(user.getId()).stream()
            .map(this::mapResponse)
            .toList();
    }

    public AssignmentResponseDTO upsertAssignment(
        User user,
        ItemType itemType,
        UUID itemId,
        UpsertAssignmentRequestDTO body
    ) {
        Folder folder = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(body.folderId(), user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found", HttpStatus.NOT_FOUND));

        Assignment assignment = assignmentRepository.findByOwner_IdAndItemTypeAndItemId(user.getId(), itemType, itemId)
            .orElseGet(Assignment::new);

        assignment.setOwner(user);
        assignment.setFolder(folder);
        assignment.setItemType(itemType);
        assignment.setItemId(itemId);
        assignment.setPosition(body.position());

        return mapResponse(assignmentRepository.save(assignment));
    }

    public void removeAssignment(User user, ItemType itemType, UUID itemId) {
        assignmentRepository.deleteByOwner_IdAndItemTypeAndItemId(user.getId(), itemType, itemId);
    }

    public AssignmentResponseDTO mapResponse(Assignment assignment) {
        return new AssignmentResponseDTO(
            assignment.getId(),
            assignment.getOwner().getId(),
            assignment.getFolder().getId(),
            assignment.getItemType(),
            assignment.getItemId(),
            assignment.getPosition(),
            assignment.getCreatedAt(),
            assignment.getUpdatedAt(),
            assignment.getVersion()
        );
    }
}
