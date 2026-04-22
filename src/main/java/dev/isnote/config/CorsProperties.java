package dev.isnote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "isnote.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        if (allowedOrigins == null) {
            allowedOrigins = List.of("http://localhost:5173", "tauri://localhost");
        }
    }
}
