package dev.isnote.user;

public record UserResponseDTO(
    String name,
    String email,
    String avatarStorageKey

) {
}
