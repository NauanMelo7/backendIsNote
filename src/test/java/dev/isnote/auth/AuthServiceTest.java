package dev.isnote.auth;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.RoleUser;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthSessionRepository authSessionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Nauan", "nauan@isnote.dev", "encoded-password", RoleUser.USER);
        user.setId(UUID.randomUUID());
    }

    @Test
    void shouldRegisterNewUserAndReturnTokens() {
        AuthRegisterDTO request = new AuthRegisterDTO("Nauan", "NAUAN@isnote.dev", "12345678");
        when(userRepository.existsByEmailIgnoreCase("nauan@isnote.dev")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(user.getId());
            return saved;
        });
        when(jwtService.getAccessTokenExpiresAt()).thenReturn(Instant.parse("2026-04-23T10:00:00Z"));
        when(jwtService.getRefreshTokenExpiresAt()).thenReturn(Instant.parse("2026-05-23T10:00:00Z"));
        when(authSessionRepository.save(any(AuthSession.class))).thenAnswer(invocation -> {
            AuthSession session = invocation.getArgument(0);
            if (session.getId() == null) {
                session.setId(UUID.randomUUID());
            }
            return session;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class), any(UUID.class))).thenReturn("refresh-token");

        AuthTokenResponseDTO response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<AuthSession> sessionCaptor = ArgumentCaptor.forClass(AuthSession.class);
        org.mockito.Mockito.verify(userRepository).save(userCaptor.capture());
        verify(authSessionRepository).save(sessionCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("nauan@isnote.dev");
        assertThat(sessionCaptor.getValue().getId()).isNotNull();
        assertThat(sessionCaptor.getValue().getRefreshTokenHash()).isNotBlank();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.user().email()).isEqualTo("nauan@isnote.dev");
    }

    @Test
    void shouldRejectInvalidCredentialsOnLogin() {
        AuthLoginDTO request = new AuthLoginDTO("nauan@isnote.dev", "wrong-password");
        when(userRepository.findByEmailIgnoreCase("nauan@isnote.dev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Invalid email or password.");
    }

    @Test
    void shouldRotateRefreshToken() {
        UUID sessionId = UUID.randomUUID();
        Claims claims = io.jsonwebtoken.Jwts.claims()
            .subject(user.getId().toString())
            .id(sessionId.toString())
            .build();

        AuthSession currentSession = new AuthSession(user, Instant.now().plusSeconds(3600));
        currentSession.setId(sessionId);
        currentSession.setRefreshTokenHash(hash("refresh-token"));

        when(jwtService.parseAndValidate("refresh-token", TokenType.REFRESH)).thenReturn(claims);
        when(jwtService.extractUserId(claims)).thenReturn(user.getId());
        when(jwtService.extractSessionId(claims)).thenReturn(sessionId);
        when(authSessionRepository.findByIdAndUser_Id(sessionId, user.getId())).thenReturn(Optional.of(currentSession));
        when(jwtService.getAccessTokenExpiresAt()).thenReturn(Instant.parse("2026-04-23T10:00:00Z"));
        when(jwtService.getRefreshTokenExpiresAt()).thenReturn(Instant.parse("2026-05-23T10:00:00Z"));
        when(authSessionRepository.save(any(AuthSession.class))).thenAnswer(invocation -> {
            AuthSession session = invocation.getArgument(0);
            if (session.getId() == null) {
                session.setId(UUID.randomUUID());
            }
            return session;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(eq(user), any(UUID.class))).thenReturn("refresh-token");

        AuthTokenResponseDTO response = authService.refresh(new AuthRefreshDTO("refresh-token"));

        assertThat(currentSession.isRevoked()).isTrue();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
