package dev.isnote.contentkey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserContentKeyRepository extends JpaRepository<UserContentKey, UUID> {

    Optional<UserContentKey> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}
