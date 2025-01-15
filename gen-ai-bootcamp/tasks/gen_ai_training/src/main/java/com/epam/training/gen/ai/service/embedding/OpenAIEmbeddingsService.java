package com.epam.training.gen.ai.service.embedding;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIEmbeddingsService {

    @Value("${embeddings-model-name}")
    private String embeddingsModelName;

    private final OpenAIAsyncClient openAIAsyncClient;

    public List<EmbeddingItem> getEmbeddings(EmbeddingsOptions embeddingsOptions) {
        return openAIAsyncClient
                .getEmbeddings(embeddingsModelName, embeddingsOptions)
                .block()
                .getData();
    }
}
