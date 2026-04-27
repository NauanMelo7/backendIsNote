package dev.isnote.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {

    Optional<AuthSession> findByIdAndUser_Id(UUID id, UUID userId);

    List<AuthSession> findAllByUser_IdAndRevokedAtIsNull(UUID userId);
}
