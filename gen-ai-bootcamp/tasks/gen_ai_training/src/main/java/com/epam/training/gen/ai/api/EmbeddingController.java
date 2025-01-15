package com.epam.training.gen.ai.api;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.service.embedding.EmbeddingsService;
import com.epam.training.gen.ai.model.api.request.EmbeddingRequest;
import com.epam.training.gen.ai.model.api.response.ApiResponse;
import com.epam.training.gen.ai.model.api.response.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/embeddings")
public class EmbeddingController {

    private final EmbeddingsService embeddingsService;

    @PostMapping
    public ResponseEntity<Void> buildAndStoreEmbedding(@RequestBody EmbeddingRequest embeddingRequest) {
        log.info("Received request to build and store embedding");
        embeddingsService.buildAndStoreEmbedding(embeddingRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/build")
    public ResponseEntity<ApiResponse<List<EmbeddingItem>>> buildEmbedding(@RequestBody EmbeddingRequest embeddingRequest) {
        log.info("Received embedding request: [{}]", embeddingRequest);
        return ResponseEntity.ok(new ApiResponse<>(embeddingsService.buildEmbedding(embeddingRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmbeddingResponse>>> searchClosestEmbedding(@RequestParam String text) {
        log.info("Received request to search closest embedding");
        return ResponseEntity.ok(new ApiResponse<>(embeddingsService.searchClosestEmbedding(text)));
    }
}
