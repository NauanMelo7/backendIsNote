package dev.isnote.auth;

import java.time.Instant;

public record AuthTokenResponseDTO(
    String accessToken,
    Instant accessTokenExpiresAt,
    String refreshToken,
    Instant refreshTokenExpiresAt,
    AuthenticatedUserDTO user
) {
}
