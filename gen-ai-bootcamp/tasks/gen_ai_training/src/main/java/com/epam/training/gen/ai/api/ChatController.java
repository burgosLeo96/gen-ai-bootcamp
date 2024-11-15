package com.epam.training.gen.ai.api;

import com.epam.training.gen.ai.service.AzureChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat")
public class ChatController {

    private final AzureChatCompletionService chatCompletionService;

    @PostMapping
    public List<String> generateChat(@RequestParam(value = "prompt") String prompt) {
        return this.chatCompletionService.getChatResponse(prompt);
    }
}
