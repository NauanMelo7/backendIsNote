package dev.isnote.ai.provider;

import dev.isnote.ai.domain.AiCapability;
import dev.isnote.ai.domain.ProviderType;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;

public interface AiProviderAdapter {

    ProviderType provider();

    boolean supports(AiCapability capability, String model);

    AiExecutionResult execute(AiExecutionRequest request);
}
