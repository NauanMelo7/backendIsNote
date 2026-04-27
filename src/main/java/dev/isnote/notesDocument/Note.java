package dev.isnote.notesDocument;

import dev.isnote.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notes_document")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "encrypted_payload", nullable = false)
    private byte[] encryptedPayload;

    @Column(name = "content_nonce", nullable = false, length = 250)
    private String contentNonce;

    @Column(name = "encryption_version", nullable = false, length = 50)
    private String encryptionVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_visibility", nullable = false, length = 50)
    private NoteShareVisibility shareVisibility;

    @Column(name = "share_id")
    private UUID shareId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;




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


}
