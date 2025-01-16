package com.epam.training.gen.ai.api;

import com.epam.training.gen.ai.model.api.response.ApiResponse;
import com.epam.training.gen.ai.service.rag.RagService;
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
@RequestMapping("/v1/rag/chat")
public class RAGController {

    private final RagService ragService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<String>>> processPrompt(@RequestParam(value = "prompt") String prompt) {
        log.info("Received RAG chat prompt: [{}]", prompt);
        var ragResponse = this.ragService.process(prompt);
        return ResponseEntity.ok(new ApiResponse<>(ragResponse));
    }

}
