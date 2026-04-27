package dev.isnote.auth;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NAME = "name";

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.accessTokenTtl());

        return Jwts.builder()
            .issuer(properties.issuer())
            .subject(user.getId().toString())
            .claim(CLAIM_TOKEN_TYPE, TokenType.ACCESS.name())
            .claim(CLAIM_ROLE, user.getRole().name())
            .claim(CLAIM_NAME, user.getName())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact();
    }

    public String generateRefreshToken(User user, UUID sessionId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.refreshTokenTtl());

        return Jwts.builder()
            .issuer(properties.issuer())
            .subject(user.getId().toString())
            .id(sessionId.toString())
            .claim(CLAIM_TOKEN_TYPE, TokenType.REFRESH.name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact();
    }

    public Claims parseAndValidate(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            TokenType tokenType = TokenType.valueOf(claims.get(CLAIM_TOKEN_TYPE, String.class));
            if (tokenType != expectedType) {
                throw unauthorized("Invalid token type.");
            }

            return claims;
        } catch (IllegalArgumentException | JwtException exception) {
            throw unauthorized("Invalid or expired token.");
        }
    }

    public UUID extractUserId(Claims claims) {
        try {
            return UUID.fromString(claims.getSubject());
        } catch (IllegalArgumentException exception) {
            throw unauthorized("Invalid token subject.");
        }
    }

    public UUID extractSessionId(Claims claims) {
        try {
            return UUID.fromString(claims.getId());
        } catch (IllegalArgumentException exception) {
            throw unauthorized("Invalid token session.");
        }
    }

    public Instant getAccessTokenExpiresAt() {
        return Instant.now().plus(properties.accessTokenTtl());
    }

    public Instant getRefreshTokenExpiresAt() {
        return Instant.now().plus(properties.refreshTokenTtl());
    }

    private BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED);
    }
}
