package dev.isnote.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "isnote.auth.jwt")
public record JwtProperties(
    @NotBlank String issuer,
    @NotBlank String secret,
    Duration accessTokenTtl,
    Duration refreshTokenTtl
) {

    public JwtProperties {
        accessTokenTtl = accessTokenTtl == null ? Duration.ofMinutes(15) : accessTokenTtl;
        refreshTokenTtl = refreshTokenTtl == null ? Duration.ofDays(30) : refreshTokenTtl;
    }
}
