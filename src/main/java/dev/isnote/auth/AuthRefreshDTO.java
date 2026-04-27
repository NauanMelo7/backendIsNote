package dev.isnote.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRefreshDTO(
    @NotBlank(message = "Refresh token is required.")
    String refreshToken
) {
}
