package com.epam.training.gen.ai.config.services;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.model.api.request.EmbeddingRequest;
import com.epam.training.gen.ai.model.api.response.EmbeddingResponse;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
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
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingsService {

    public static final int POINTS_LIMIT = 5;

    @Value("${collection-name}")
    private String collectionName;

    @Value("${embeddings-model-name}")
    private String embeddingsModelName;

    private final QdrantClient qdrantClient;
    private final OpenAIAsyncClient openAIAsyncClient;

    public List<EmbeddingItem> buildEmbedding(EmbeddingRequest embeddingRequest) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(embeddingRequest.text()));
        return getEmbeddings(embeddingsOptions);
    }

    public void buildAndStoreEmbedding(EmbeddingRequest embeddingRequest) {
        String text = embeddingRequest.text();
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(text));
        List<EmbeddingItem> embeddings = getEmbeddings(embeddingsOptions);
        List<Points.PointStruct> vector = convertEmbeddingToVector(embeddings, text);
        saveVector(vector);
        log.info("Embedding for text [{}] stored", text);
    }

    public List<EmbeddingResponse> searchClosestEmbedding(String text) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(List.of(text));
        List<EmbeddingItem> embeddings = getEmbeddings(embeddingsOptions);
        return searchEmbeddings(embeddings);
    }

    private List<EmbeddingResponse> searchEmbeddings(List<EmbeddingItem> embeddings) {

        List<Float> vector = embeddings.stream().map(EmbeddingItem::getEmbedding).flatMap(List::stream).toList();

        Points.SearchPoints searchPoints = Points.SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(vector)
                .setLimit(POINTS_LIMIT)
                .build();

        try {
            List<Points.ScoredPoint> scoredPoints = qdrantClient.searchAsync(searchPoints).get();
            return scoredPoints.stream().map(EmbeddingResponse::fromScoredPoints).toList();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Error while searching for closest embedding", e);
            throw new InternalServerErrorException("Error while searching for closest embedding");
        }
    }

    private void saveVector(List<Points.PointStruct> vector) {
        try {
            var result = qdrantClient.upsertAsync(collectionName, vector).get();
            log.info("Upsert result: [{}]", result.getStatus().getNumber());
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Error while saving vector", e);
            throw new InternalServerErrorException("Error while saving vector");
        }
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

    private List<EmbeddingItem> getEmbeddings(EmbeddingsOptions embeddingsOptions) {
        return openAIAsyncClient
                .getEmbeddings(embeddingsModelName, embeddingsOptions)
                .block()
                .getData();
    }

}
