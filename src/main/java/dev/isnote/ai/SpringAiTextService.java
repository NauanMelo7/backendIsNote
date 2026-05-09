package dev.isnote.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SpringAiTextService {

    private final ChatClient chatClient;
    public SpringAiTextService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generate(String prompt) {
        return chatClient
            .prompt()
            .user(prompt)
            .call()
            .content();
    };

    /*
    public String generate(String prompt) {
    var request = chatClient.prompt();
    var userMessage = request.user(prompt);
    var response = userMessage.call();
    return response.content();
    }
    * */
}
