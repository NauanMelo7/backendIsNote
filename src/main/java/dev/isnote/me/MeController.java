package dev.isnote.me;

import dev.isnote.auth.AuthTokenResponseDTO;
import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping("/profile")
    public ResponseEntity<MeProfileResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(meService.getProfile(user));
    }

    @PatchMapping("/profile")
    public ResponseEntity<MeProfileResponseDTO> updateProfile(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody UpdateProfileDTO body
    ) {
        return ResponseEntity.ok(meService.updateProfile(user, body));
    }

    @PostMapping("/security/change-password")
    public ResponseEntity<AuthTokenResponseDTO> changePassword(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody ChangePasswordDTO body
    ) {
        return ResponseEntity.ok(meService.changePassword(user, body));
    }
}
