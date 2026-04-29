package dev.isnote.contentkey;

import dev.isnote.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_content_key")
public class UserContentKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "wrapped_key", nullable = false)
    private byte[] wrappedKey;

    @Column(name = "wrapping_salt", nullable = false)
    private byte[] wrappingSalt;

    @Column(name = "wrapping_nonce", nullable = false)
    private byte[] wrappingNonce;

    @Column(name = "kdf_algorithm", nullable = false, length = 50)
    private String kdfAlgorithm;

    @Column(name = "kdf_memory_cost_kib", nullable = false)
    private int kdfMemoryCostKib;

    @Column(name = "kdf_iterations", nullable = false)
    private int kdfIterations;

    @Column(name = "kdf_parallelism", nullable = false)
    private int kdfParallelism;

    @Column(name = "content_encryption_version", nullable = false, length = 50)
    private String contentEncryptionVersion;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getWrappedKey() {
        return wrappedKey;
    }

    public void setWrappedKey(byte[] wrappedKey) {
        this.wrappedKey = wrappedKey;
    }

    public byte[] getWrappingSalt() {
        return wrappingSalt;
    }

    public void setWrappingSalt(byte[] wrappingSalt) {
        this.wrappingSalt = wrappingSalt;
    }

    public byte[] getWrappingNonce() {
        return wrappingNonce;
    }

    public void setWrappingNonce(byte[] wrappingNonce) {
        this.wrappingNonce = wrappingNonce;
    }

    public String getKdfAlgorithm() {
        return kdfAlgorithm;
    }

    public void setKdfAlgorithm(String kdfAlgorithm) {
        this.kdfAlgorithm = kdfAlgorithm;
    }

    public int getKdfMemoryCostKib() {
        return kdfMemoryCostKib;
    }

    public void setKdfMemoryCostKib(int kdfMemoryCostKib) {
        this.kdfMemoryCostKib = kdfMemoryCostKib;
    }

    public int getKdfIterations() {
        return kdfIterations;
    }

    public void setKdfIterations(int kdfIterations) {
        this.kdfIterations = kdfIterations;
    }

    public int getKdfParallelism() {
        return kdfParallelism;
    }

    public void setKdfParallelism(int kdfParallelism) {
        this.kdfParallelism = kdfParallelism;
    }

    public String getContentEncryptionVersion() {
        return contentEncryptionVersion;
    }

    public void setContentEncryptionVersion(String contentEncryptionVersion) {
        this.contentEncryptionVersion = contentEncryptionVersion;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
