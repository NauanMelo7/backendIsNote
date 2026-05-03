package dev.isnote.ai.provider.dto;

import dev.isnote.ai.domain.AiCapability;
import dev.isnote.ai.domain.AiTaskType;
import dev.isnote.ai.domain.ProviderType;

public record AiExecutionRequest(
    ProviderType providerType,
    AiCapability aiCapability,
    AiTaskType aiTaskType,
    String model,
    String systemPrompt,
    String userPrompt
) {
}
