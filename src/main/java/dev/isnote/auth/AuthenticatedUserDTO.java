package dev.isnote.auth;

import java.util.UUID;

public record AuthenticatedUserDTO(
    UUID id,
    String name,
    String email,
    String role
) {
}
