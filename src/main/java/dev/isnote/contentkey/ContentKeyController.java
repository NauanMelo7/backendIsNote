package dev.isnote.contentkey;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/security/content-key")
public class ContentKeyController {

    private final ContentKeyService contentKeyService;

    public ContentKeyController(ContentKeyService contentKeyService) {
        this.contentKeyService = contentKeyService;
    }

    @GetMapping
    public ResponseEntity<ContentKeyEnvelopeDTO> get(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contentKeyService.getForUser(user));
    }

    @PutMapping
    public ResponseEntity<ContentKeyEnvelopeDTO> upsert(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody UpsertContentKeyDTO body
    ) {
        return ResponseEntity.ok(contentKeyService.upsertForUser(user, body));
    }
}
