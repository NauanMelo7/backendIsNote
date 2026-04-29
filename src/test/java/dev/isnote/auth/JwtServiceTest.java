package dev.isnote.auth;

import dev.isnote.user.RoleUser;
import dev.isnote.user.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
        new JwtProperties(
            "isnote-test",
            "change-me-in-tests-change-me-in-tests-change-me",
            Duration.ofMinutes(15),
            Duration.ofDays(30)
        )
    );

    @Test
    void shouldGenerateAndParseAccessToken() {
        User user = new User("Nauan", "nauan@isnote.app", "encoded", RoleUser.USER);
        user.setId(UUID.randomUUID());

        String token = jwtService.generateAccessToken(user);
        Claims claims = jwtService.parseAndValidate(token, TokenType.ACCESS);

        assertThat(jwtService.extractUserId(claims)).isEqualTo(user.getId());
        assertThat(claims.get("role", String.class)).isEqualTo(RoleUser.USER.name());
        assertThat(claims.get("name", String.class)).isEqualTo("Nauan");
    }

    @Test
    void shouldGenerateAndParseRefreshToken() {
        User user = new User("Nauan", "nauan@isnote.app", "encoded", RoleUser.USER);
        user.setId(UUID.randomUUID());
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateRefreshToken(user, sessionId);
        Claims claims = jwtService.parseAndValidate(token, TokenType.REFRESH);

        assertThat(jwtService.extractUserId(claims)).isEqualTo(user.getId());
        assertThat(jwtService.extractSessionId(claims)).isEqualTo(sessionId);
    }

    @Test
    void shouldRejectWrongTokenType() {
        User user = new User("Nauan", "nauan@isnote.app", "encoded", RoleUser.USER);
        user.setId(UUID.randomUUID());

        String token = jwtService.generateAccessToken(user);

        assertThatThrownBy(() -> jwtService.parseAndValidate(token, TokenType.REFRESH))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid token type.");
    }
}
