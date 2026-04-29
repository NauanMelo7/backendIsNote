package dev.isnote.contentkey;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ContentKeyService {

    private final UserRepository userRepository;
    private final UserContentKeyRepository userContentKeyRepository;

    public ContentKeyService(UserRepository userRepository, UserContentKeyRepository userContentKeyRepository) {
        this.userRepository = userRepository;
        this.userContentKeyRepository = userContentKeyRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ContentKeyEnvelopeDTO getForUser(User authenticatedUser) {
        User user = loadActiveUser(authenticatedUser);
        UserContentKey contentKey = userContentKeyRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new BusinessException("Content key envelope was not found.", HttpStatus.NOT_FOUND));

        return mapEnvelope(contentKey);
    }

    public ContentKeyEnvelopeDTO upsertForUser(User authenticatedUser, UpsertContentKeyDTO body) {
        User user = loadActiveUser(authenticatedUser);
        return upsertForLoadedUser(user, body);
    }

    public ContentKeyEnvelopeDTO upsertForLoadedUser(User user, UpsertContentKeyDTO body) {
        UserContentKey contentKey = userContentKeyRepository.findByUser_Id(user.getId())
            .orElseGet(UserContentKey::new);

        contentKey.setUser(user);
        contentKey.setWrappedKey(body.wrappedKey());
        contentKey.setWrappingSalt(body.wrappingSalt());
        contentKey.setWrappingNonce(body.wrappingNonce());
        contentKey.setKdfAlgorithm(body.kdfAlgorithm().trim());
        contentKey.setKdfMemoryCostKib(body.kdfMemoryCostKib());
        contentKey.setKdfIterations(body.kdfIterations());
        contentKey.setKdfParallelism(body.kdfParallelism());
        contentKey.setContentEncryptionVersion(body.contentEncryptionVersion().trim());

        UserContentKey saved = userContentKeyRepository.save(contentKey);
        return mapEnvelope(saved);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean existsForUser(User authenticatedUser) {
        return userContentKeyRepository.existsByUser_Id(authenticatedUser.getId());
    }

    private User loadActiveUser(User authenticatedUser) {
        return userRepository.findById(authenticatedUser.getId())
            .filter(User::isEnabled)
            .orElseThrow(() -> new BusinessException("Authenticated user was not found.", HttpStatus.UNAUTHORIZED));
    }

    private ContentKeyEnvelopeDTO mapEnvelope(UserContentKey contentKey) {
        return new ContentKeyEnvelopeDTO(
            contentKey.getWrappedKey(),
            contentKey.getWrappingSalt(),
            contentKey.getWrappingNonce(),
            contentKey.getKdfAlgorithm(),
            contentKey.getKdfMemoryCostKib(),
            contentKey.getKdfIterations(),
            contentKey.getKdfParallelism(),
            contentKey.getContentEncryptionVersion(),
            contentKey.getCreatedAt(),
            contentKey.getUpdatedAt(),
            contentKey.getVersion()
        );
    }
}
