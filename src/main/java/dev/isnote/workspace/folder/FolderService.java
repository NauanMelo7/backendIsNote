package dev.isnote.workspace.folder;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.workspace.assignment.AssignmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final AssignmentRepository assignmentRepository;

    public FolderService(FolderRepository folderRepository, AssignmentRepository assignmentRepository) {
        this.folderRepository = folderRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public FolderResponseDTO createFolder(User user, CreateFolderRequestDTO body) {
        Folder folderParent = resolveActiveParent(user, body.parentFolderId());

        Folder folder = new Folder();
        folder.setOwner(user);
        folder.setFolderParent(folderParent);
        folder.setEncryptedPayload(body.encryptedPayload());
        folder.setContentNonce(body.contentNonce());
        folder.setEncryptionVersion(body.encryptionVersion());

        return mapFolderResponse(folderRepository.save(folder));
    }

    public FolderResponseDTO updateFolder(User user, UUID folderId, UpdateFolderRequestDTO body) {
        Folder folder = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(folderId, user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found", HttpStatus.NOT_FOUND));

        if (!folder.getVersion().equals(body.version())) {
            throw new BusinessException("Folder was updated by another user", HttpStatus.CONFLICT);
        }

        Folder nextParent = resolveUpdateParent(user, folderId, body.parentFolderId());
        folder.setFolderParent(nextParent);
        folder.setEncryptedPayload(body.encryptedPayload());
        folder.setContentNonce(body.contentNonce());
        folder.setEncryptionVersion(body.encryptionVersion());

        try {
            return mapFolderResponse(folderRepository.save(folder));
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new BusinessException("Folder was updated by another user", HttpStatus.CONFLICT);
        }
    }

    public List<FolderResponseDTO> listActiveFolders(User user) {
        return folderRepository.findAllByOwner_IdAndDeletedAtIsNullOrderByCreatedAtAsc(user.getId()).stream()
            .map(this::mapFolderResponse)
            .toList();
    }

    public List<FolderResponseDTO> listTrashFolders(User user) {
        return folderRepository.findAllByOwner_IdAndDeletedAtIsNotNullOrderByDeletedAtDesc(user.getId()).stream()
            .map(this::mapFolderResponse)
            .toList();
    }

    public FolderResponseDTO getFolder(User user, UUID folderId) {
        Folder folder = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(folderId, user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found", HttpStatus.NOT_FOUND));
        return mapFolderResponse(folder);
    }

    public void trashFolder(User user, UUID folderId) {
        Folder root = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(folderId, user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found", HttpStatus.NOT_FOUND));

        List<Folder> activeFolders = folderRepository.findAllByOwner_IdAndDeletedAtIsNullOrderByCreatedAtAsc(user.getId());
        List<Folder> subtree = collectSubtree(activeFolders, root.getId());
        Instant deletedAt = Instant.now();

        subtree.forEach(folder -> folder.setDeletedAt(deletedAt));
        folderRepository.saveAll(subtree);
    }

    public FolderResponseDTO restoreFolder(User user, UUID folderId) {
        Folder root = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNotNull(folderId, user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found in trash", HttpStatus.NOT_FOUND));

        List<Folder> allFolders = folderRepository.findAllByOwner_IdOrderByCreatedAtAsc(user.getId());
        List<Folder> subtree = collectSubtree(allFolders, root.getId());

        subtree.stream()
            .filter(folder -> folder.getDeletedAt() != null)
            .forEach(folder -> folder.setDeletedAt(null));

        folderRepository.saveAll(subtree);
        return mapFolderResponse(root);
    }

    public void deleteFolderPermanently(User user, UUID folderId) {
        Folder root = folderRepository.findByIdAndOwner_IdAndDeletedAtIsNotNull(folderId, user.getId())
            .orElseThrow(() -> new BusinessException("Folder not found in trash", HttpStatus.NOT_FOUND));

        List<Folder> allFolders = folderRepository.findAllByOwner_IdOrderByCreatedAtAsc(user.getId());
        List<Folder> subtree = collectSubtree(allFolders, root.getId());
        List<UUID> folderIds = subtree.stream().map(Folder::getId).toList();

        assignmentRepository.deleteAllByOwner_IdAndFolder_IdIn(user.getId(), folderIds);

        List<Folder> deleteOrder = new ArrayList<>(subtree);
        Collections.reverse(deleteOrder);
        folderRepository.deleteAll(deleteOrder);
    }

    public FolderResponseDTO mapFolderResponse(Folder folder) {
        return new FolderResponseDTO(
            folder.getId(),
            folder.getOwner().getId(),
            folder.getFolderParent() != null ? folder.getFolderParent().getId() : null,
            folder.getEncryptedPayload(),
            folder.getContentNonce(),
            folder.getEncryptionVersion(),
            folder.getCreatedAt(),
            folder.getUpdatedAt(),
            folder.getDeletedAt(),
            folder.getVersion()
        );
    }

    private Folder resolveActiveParent(User user, UUID parentFolderId) {
        if (parentFolderId == null) {
            return null;
        }

        return folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(parentFolderId, user.getId())
            .orElseThrow(() -> new BusinessException("Parent folder not found", HttpStatus.NOT_FOUND));
    }

    private Folder resolveUpdateParent(User user, UUID folderId, UUID parentFolderId) {
        if (parentFolderId == null) {
            return null;
        }

        if (folderId.equals(parentFolderId)) {
            throw new BusinessException("Folder cannot be its own parent", HttpStatus.BAD_REQUEST);
        }

        Folder nextParent = resolveActiveParent(user, parentFolderId);
        List<Folder> activeFolders = folderRepository.findAllByOwner_IdAndDeletedAtIsNullOrderByCreatedAtAsc(user.getId());
        List<UUID> blockedIds = collectSubtree(activeFolders, folderId).stream()
            .map(Folder::getId)
            .toList();

        if (blockedIds.contains(parentFolderId)) {
            throw new BusinessException("Folder cannot be moved into its own subtree", HttpStatus.BAD_REQUEST);
        }

        return nextParent;
    }

    private List<Folder> collectSubtree(List<Folder> folders, UUID rootId) {
        Map<UUID, List<Folder>> byParentId = new HashMap<>();
        for (Folder folder : folders) {
            UUID parentId = folder.getFolderParent() != null ? folder.getFolderParent().getId() : null;
            byParentId.computeIfAbsent(parentId, ignored -> new ArrayList<>()).add(folder);
        }

        Folder root = folders.stream()
            .filter(folder -> folder.getId().equals(rootId))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Folder not found", HttpStatus.NOT_FOUND));

        List<Folder> subtree = new ArrayList<>();
        ArrayDeque<Folder> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Folder current = queue.removeFirst();
            subtree.add(current);

            for (Folder child : byParentId.getOrDefault(current.getId(), List.of())) {
                queue.addLast(child);
            }
        }

        return subtree;
    }
}
