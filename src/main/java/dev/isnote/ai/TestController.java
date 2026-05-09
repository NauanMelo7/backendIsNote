package dev.isnote.ai;

import dev.isnote.ai.orchestration.AiOrchestrator;
import dev.isnote.ai.provider.dto.AiExecutionRequest;
import dev.isnote.ai.provider.dto.AiExecutionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class TestController {

    private final AiOrchestrator aiOrchestrator;
    private final SpringAiTextService springAiTextService;
    public TestController(AiOrchestrator aiOrchestrator, SpringAiTextService springAiTextService) {
        this.aiOrchestrator = aiOrchestrator;
        this.springAiTextService = springAiTextService;
    }

    @PostMapping("/test")
    public ResponseEntity<AiExecutionResult> executeTest(@RequestBody AiExecutionRequest aiExecutionRequest) {
        return ResponseEntity.ok(this.aiOrchestrator.result(aiExecutionRequest));
    }

    @PostMapping("/testeGem")
    public ResponseEntity<String> generate(@RequestBody String prompt) {
        return ResponseEntity.status(HttpStatus.OK).body(this.springAiTextService.generate(prompt));
    }
}
