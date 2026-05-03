package dev.isnote.ai.provider.fake;

import dev.isnote.ai.domain.AiCapability;
import dev.isnote.ai.domain.ProviderType;
import dev.isnote.ai.provider.AiProviderAdapter;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;
import dev.isnote.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AiProviderFake implements AiProviderAdapter {


    @Override
    public ProviderType provider() {
        return ProviderType.GEMINI;
    }

    @Override
    public boolean supports(AiCapability capability, String model) {
        return true;
    }

    @Override
    public AiExecutionResult execute(AiExecutionRequest request) {
        if(request.aiCapability() == AiCapability.TEXT_GENERATION && request.providerType() == provider()) {
            return new AiExecutionResult("text generate", null, "fake but good");
        }

        if(request.aiCapability() == AiCapability.IMAGE_GENERATION) {
            return new AiExecutionResult(null, "image.com/url", "fake but good");
        }

        throw new BusinessException("This provider does not support the requested capability", HttpStatus.CONFLICT);
    }
}
