package com.example.springbootaidemo.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/")
    public String home() {
        return "Spring Boot AI demo — try GET /api/chat?message=Hello";
    }

    @GetMapping("/api/chat")
    public String chat(
            @RequestParam(value = "message", defaultValue = "Say hello in one short sentence.") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
