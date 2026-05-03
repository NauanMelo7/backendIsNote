package dev.isnote.ai;

import dev.isnote.ai.orchestration.AiOrchestrator;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;
import dev.isnote.ai.provider.fake.AiProviderFake;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class TestController {

    private final AiOrchestrator aiOrchestrator;
    public TestController(AiOrchestrator aiOrchestrator) {
        this.aiOrchestrator = aiOrchestrator;
    }

    @PostMapping("/test")
    public ResponseEntity<AiExecutionResult> executeTest(@RequestBody AiExecutionRequest aiExecutionRequest) {
        return ResponseEntity.ok(this.aiOrchestrator.result(aiExecutionRequest));
    }
}
