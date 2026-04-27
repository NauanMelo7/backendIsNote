package dev.isnote.auth;

import dev.isnote.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/status")
    public ResponseEntity<AuthStatusResponseDTO> status() {
        return ResponseEntity.ok(new AuthStatusResponseDTO("ok", "jwt"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponseDTO> register(@Valid @RequestBody AuthRegisterDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(body));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDTO> login(@Valid @RequestBody AuthLoginDTO body) {
        return ResponseEntity.ok(authService.login(body));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponseDTO> refresh(@Valid @RequestBody AuthRefreshDTO body) {
        return ResponseEntity.ok(authService.refresh(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthLogoutDTO body) {
        authService.logout(body);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.me(user));
    }
}
