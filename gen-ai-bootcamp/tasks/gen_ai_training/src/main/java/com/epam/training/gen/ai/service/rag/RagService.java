package com.epam.training.gen.ai.service.rag;

import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.service.QDrant.QDrantService;
import com.epam.training.gen.ai.service.chatcompletion.utils.MessageProcessingUtils;
import com.epam.training.gen.ai.service.embedding.OpenAIEmbeddingsService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private static final Integer POINTS_LIMIT = 10;

    @Value("${rag-collection-name}")
    private String collectionName;

    private final Kernel kernel;
    private final QDrantService qDrantService;
    private final InvocationContext invocationContext;
    private final ChatHistory chatHistory = new ChatHistory();
    private final ChatCompletionService chatCompletionService;
    private final OpenAIEmbeddingsService openAIEmbeddingsService;

    public List<String> process(String prompt) {
        log.info("Processing prompt request");

        List<Float> userPromptEmbedding = embedText(prompt);
        List<String> context = getPromptClosestContext(userPromptEmbedding);

        String chatContext = String.join("\n", context);
        chatHistory.addSystemMessage(String.format("You are a helpful assistant. Use the following context to answer the user's question:\n %s", chatContext));
        chatHistory.addUserMessage(prompt);

        var chatResponse = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block();

        var assistantResponse = MessageProcessingUtils.processChatResponse(chatResponse);
        this.chatHistory.addAll(assistantResponse);
        return MessageProcessingUtils.getAssistantResponseMessagesContent(assistantResponse);
    }

    private List<String> getPromptClosestContext(List<Float> vector) {
        Points.SearchPoints searchPoints = Points.SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(vector)
                .setLimit(POINTS_LIMIT)
                .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true).build())
                .build();

        List<Points.ScoredPoint> scoredPoints = qDrantService.searchEmbeddings(searchPoints);

        return scoredPoints
                .stream()
                .map(Points.ScoredPoint::getPayloadMap)
                .map(payload -> payload.get("prompt"))
                .map(String::valueOf)
                .toList();
    }

    private List<Float> embedText(String prompt) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(prompt));
        List<EmbeddingItem> embeddingItems = openAIEmbeddingsService.getEmbeddings(embeddingsOptions);
        return embeddingItems.stream().map(EmbeddingItem::getEmbedding).flatMap(List::stream).toList();
    }

}
