package dev.isnote.auth;

import dev.isnote.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {
}
