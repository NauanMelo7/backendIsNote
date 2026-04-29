package dev.isnote.contentkey;

import java.time.Instant;

public record ContentKeyEnvelopeDTO(
    byte[] wrappedKey,
    byte[] wrappingSalt,
    byte[] wrappingNonce,
    String kdfAlgorithm,
    Integer kdfMemoryCostKib,
    Integer kdfIterations,
    Integer kdfParallelism,
    String contentEncryptionVersion,
    Instant createdAt,
    Instant updatedAt,
    Long version
) {
}
