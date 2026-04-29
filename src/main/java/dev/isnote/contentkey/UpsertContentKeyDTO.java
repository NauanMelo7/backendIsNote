package dev.isnote.contentkey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpsertContentKeyDTO(
    @NotNull(message = "Wrapped key is required.")
    byte[] wrappedKey,

    @NotNull(message = "Wrapping salt is required.")
    byte[] wrappingSalt,

    @NotNull(message = "Wrapping nonce is required.")
    byte[] wrappingNonce,

    @NotBlank(message = "KDF algorithm is required.")
    String kdfAlgorithm,

    @Positive(message = "KDF memory cost must be positive.")
    Integer kdfMemoryCostKib,

    @Positive(message = "KDF iterations must be positive.")
    Integer kdfIterations,

    @Positive(message = "KDF parallelism must be positive.")
    Integer kdfParallelism,

    @NotBlank(message = "Content encryption version is required.")
    String contentEncryptionVersion
) {
}
