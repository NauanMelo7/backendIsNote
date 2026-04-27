package dev.isnote.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLogoutDTO(
    @NotBlank(message = "Refresh token is required.")
    String refreshToken
) {
}
