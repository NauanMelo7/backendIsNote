package dev.isnote.ai.provider.dto;

public record AiExecutionResult(
    String text,
    String imageUrl,
    String rawResponse
) {
}
