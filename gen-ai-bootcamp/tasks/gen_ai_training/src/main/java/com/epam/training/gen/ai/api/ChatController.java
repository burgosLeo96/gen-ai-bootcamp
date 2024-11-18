package com.epam.training.gen.ai.api;

import com.epam.training.gen.ai.model.api.ApiResponse;
import com.epam.training.gen.ai.service.AzureChatCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat")
public class ChatController {

    private final AzureChatCompletionService chatCompletionService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<String>>> generateChat(@RequestParam(value = "prompt") String prompt) {
        log.info("Received chat prompt: [{}]", prompt);
        var chatResponse = this.chatCompletionService.getChatResponse(prompt);
        return ResponseEntity.ok(new ApiResponse<>(chatResponse));
    }
}
