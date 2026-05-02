package dev.isnote.workspace.folder;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public FolderResponseDTO createFolder(User user, FolderRequestDTO body) {

        Folder folderParent = null;

        if(body.folderParentId() != null) {
           folderParent = this.folderRepository.findByIdAndOwner_IdAndDeletedAtIsNull(body.folderParentId(), user.getId())
                .orElseThrow(() -> new BusinessException("Folder not found or already deleted", HttpStatus.NOT_FOUND));
        }

        Folder folder = new Folder();
        folder.setOwner(user);
        folder.setFolderParent(folderParent);
        folder.setEncryptedPayload(body.encryptedPayload());
        folder.setContentNonce(body.contentNonce());

        return mapFolderResponse(this.folderRepository.save(folder));
    }

    public FolderResponseDTO mapFolderResponse(Folder folder){
        return new FolderResponseDTO(
            folder.getId(),
            folder.getOwner().getId(),
            folder.getFolderParent() != null ? folder.getFolderParent().getId() : null,
            folder.getEncryptedPayload(),
            folder.getContentNonce(),
            folder.getCreatedAt(),
            folder.getUpdatedAt(),
            folder.getDeletedAt(),
            folder.getVersion()
        );
    }
}
