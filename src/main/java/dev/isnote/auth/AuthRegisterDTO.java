package dev.isnote.auth;

public record AuthRegisterDTO(
    String name,
    String email,
    String password
) {
}
