package dev.isnote.ai.orchestration;

import dev.isnote.ai.domain.AiCapability;
import dev.isnote.ai.domain.AiTaskType;
import dev.isnote.ai.domain.ProviderType;
import dev.isnote.ai.provider.AiProviderAdapter;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiOrchestratorTest {

    @Test
    void shouldExecuteProviderWhenProviderSupportsRequest() {
        AiProviderAdapter adapter = mock(AiProviderAdapter.class);
        AiOrchestrator orchestrator = new AiOrchestrator(List.of(adapter));

        AiExecutionRequest request = new AiExecutionRequest(
            ProviderType.GEMINI,
            AiCapability.TEXT_GENERATION,
            AiTaskType.CHALLENGE_REVIEW,
            "gemini-2.0-flash",
            "system prompt",
            "user prompt"
        );

        AiExecutionResult result = new AiExecutionResult(
            "fake text"
        );

        when(adapter.provider()).thenReturn(ProviderType.GEMINI);
        when(adapter.supports(AiCapability.TEXT_GENERATION, "gemini-2.0-flash")).thenReturn(true);
        when(adapter.execute(request)).thenReturn(result);

        AiExecutionResult actual = orchestrator.result(request);

        assertThat(actual).isEqualTo(result);
    }
}
