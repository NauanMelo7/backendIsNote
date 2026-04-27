package dev.isnote.auth;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.RoleUser;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        AuthSessionRepository authSessionRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthTokenResponseDTO register(AuthRegisterDTO body) {
        String normalizedEmail = normalizeEmail(body.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessException("Email address already exists.", HttpStatus.CONFLICT);
        }

        User user = new User(
            body.name().trim(),
            normalizedEmail,
            passwordEncoder.encode(body.password()),
            RoleUser.USER
        );

        User savedUser = userRepository.save(user);
        return issueTokens(savedUser);
    }

    public AuthTokenResponseDTO login(AuthLoginDTO body) {
        String normalizedEmail = normalizeEmail(body.email());

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
            .filter(User::isEnabled)
            .orElseThrow(() -> invalidCredentials());

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw invalidCredentials();
        }

        return issueTokens(user);
    }

    public AuthTokenResponseDTO issueTokensFor(User user) {
        return issueTokens(user);
    }

    public AuthTokenResponseDTO refresh(AuthRefreshDTO body) {
        Claims claims = jwtService.parseAndValidate(body.refreshToken(), TokenType.REFRESH);
        UUID userId = jwtService.extractUserId(claims);
        UUID sessionId = jwtService.extractSessionId(claims);

        AuthSession session = authSessionRepository.findByIdAndUser_Id(sessionId, userId)
            .orElseThrow(() -> new BusinessException("Refresh session was not found.", HttpStatus.UNAUTHORIZED));

        Instant now = Instant.now();
        if (session.isRevoked() || session.isExpired(now)) {
            throw new BusinessException("Refresh token is no longer valid.", HttpStatus.UNAUTHORIZED);
        }

        if (!session.getRefreshTokenHash().equals(hashToken(body.refreshToken()))) {
            throw new BusinessException("Refresh token is no longer valid.", HttpStatus.UNAUTHORIZED);
        }

        session.revokeNow();
        authSessionRepository.save(session);

        return issueTokens(session.getUser());
    }

    public void logout(AuthLogoutDTO body) {
        Claims claims = jwtService.parseAndValidate(body.refreshToken(), TokenType.REFRESH);
        UUID userId = jwtService.extractUserId(claims);
        UUID sessionId = jwtService.extractSessionId(claims);

        authSessionRepository.findByIdAndUser_Id(sessionId, userId)
            .ifPresent(session -> {
                if (!session.isRevoked()) {
                    session.revokeNow();
                    authSessionRepository.save(session);
                }
            });
    }

    public void revokeActiveSessions(UUID userId) {
        List<AuthSession> activeSessions = authSessionRepository.findAllByUser_IdAndRevokedAtIsNull(userId);
        for (AuthSession session : activeSessions) {
            session.revokeNow();
        }
        authSessionRepository.saveAll(activeSessions);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public AuthenticatedUserDTO me(User user) {
        return mapUser(user);
    }

    private AuthTokenResponseDTO issueTokens(User user) {
        Instant accessTokenExpiresAt = jwtService.getAccessTokenExpiresAt();
        Instant refreshTokenExpiresAt = jwtService.getRefreshTokenExpiresAt();

        AuthSession session = new AuthSession(user, refreshTokenExpiresAt);
        String refreshToken = jwtService.generateRefreshToken(user, session.getId());
        session.setRefreshTokenHash(hashToken(refreshToken));
        session.markUsedNow();
        authSessionRepository.save(session);

        String accessToken = jwtService.generateAccessToken(user);

        return new AuthTokenResponseDTO(
            accessToken,
            accessTokenExpiresAt,
            refreshToken,
            refreshTokenExpiresAt,
            mapUser(user)
        );
    }

    private AuthenticatedUserDTO mapUser(User user) {
        return new AuthenticatedUserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private BusinessException invalidCredentials() {
        return new BusinessException("Invalid email or password.", HttpStatus.UNAUTHORIZED);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);
        }
    }
}
