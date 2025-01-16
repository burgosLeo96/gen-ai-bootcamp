package com.epam.training.gen.ai.service.embedding;

import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.model.api.request.EmbeddingRequest;
import com.epam.training.gen.ai.model.api.response.EmbeddingResponse;
import com.epam.training.gen.ai.service.QDrant.QDrantService;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingsService {

    public static final int POINTS_LIMIT = 5;

    @Value("${collection-name}")
    private String collectionName;

    private static final Integer VECTOR_SIZE = 1536;

    private final QDrantService qDrantService;
    private final OpenAIEmbeddingsService openAIEmbeddingsService;

    @PostConstruct
    public void initializeEmbeddingsCollection() {
        qDrantService.createCollection(collectionName, Collections.Distance.Euclid, VECTOR_SIZE);
    }

    public List<EmbeddingItem> buildEmbedding(EmbeddingRequest embeddingRequest) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(embeddingRequest.text()));
        return openAIEmbeddingsService.getEmbeddings(embeddingsOptions);
    }

    public void buildAndStoreEmbedding(EmbeddingRequest embeddingRequest) {
        String text = embeddingRequest.text();
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(text));
        List<EmbeddingItem> embeddings = openAIEmbeddingsService.getEmbeddings(embeddingsOptions);
        List<Points.PointStruct> vector = convertEmbeddingToVector(embeddings, text);
        saveVector(vector);
        log.info("Embedding for text [{}] stored", text);
    }

    public List<EmbeddingResponse> searchClosestEmbedding(String text) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(text));
        List<EmbeddingItem> embeddings = openAIEmbeddingsService.getEmbeddings(embeddingsOptions);
        return searchEmbeddings(embeddings);
    }

    private List<EmbeddingResponse> searchEmbeddings(List<EmbeddingItem> embeddings) {

        List<Float> vector = embeddings.stream().map(EmbeddingItem::getEmbedding).flatMap(List::stream).toList();

        Points.SearchPoints searchPoints = Points.SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(vector)
                .setLimit(POINTS_LIMIT)
                .build();

        List<Points.ScoredPoint> scoredPoints = qDrantService.searchEmbeddings(searchPoints);
        return scoredPoints.stream().map(EmbeddingResponse::fromScoredPoints).toList();
    }

    private void saveVector(List<Points.PointStruct> vector) {
        var result = qDrantService.savePoints(collectionName, vector);
        log.info("Upsert result: [{}]", result.getStatus().getNumber());
    }

    private List<Points.PointStruct> convertEmbeddingToVector(List<EmbeddingItem> embeddings, String text) {
        return embeddings
                .stream()
                .map(EmbeddingItem::getEmbedding)
                .map(points -> mapToPointStruct(points, text))
                .toList();
    }

    private static Points.PointStruct mapToPointStruct(List<Float> point, String text) {
        return Points.PointStruct.newBuilder()
                .setId(id(UUID.randomUUID()))
                .setVectors(vectors(point))
                .putAllPayload(Map.of("text", value(text)))
                .build();
    }
}
