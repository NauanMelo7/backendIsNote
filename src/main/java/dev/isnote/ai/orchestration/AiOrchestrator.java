package dev.isnote.ai.orchestration;

import dev.isnote.ai.provider.AiProviderAdapter;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;
import dev.isnote.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiOrchestrator {

    private final List<AiProviderAdapter> adapters;

    public AiOrchestrator(List<AiProviderAdapter> aiProviderAdapters) {
        this.adapters = aiProviderAdapters;
    }

    public AiExecutionResult result(AiExecutionRequest request) {

        return adapters.stream()
            .filter(adapter -> adapter.provider() == request.providerType())
            .filter(adapter -> adapter.supports(request.aiCapability(), request.model()))
            .findFirst()
            .orElseThrow(() -> new BusinessException("No AI provider supports model this request.", HttpStatus.CONFLICT))
            .execute(request);


    }

}
