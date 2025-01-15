package com.epam.training.gen.ai.config.rag;

import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.service.QDrant.QDrantService;
import com.epam.training.gen.ai.service.embedding.OpenAIEmbeddingsService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.segment.TextSegment;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RAGConfig {

    @Value("${rag-collection-name}")
    private String collectionName;

    private final QDrantService qDrantService;
    private final OpenAIEmbeddingsService openAIEmbeddingsService;

    private static final Integer VECTOR_SIZE = 1536;
    private static final Integer MAX_SEGMENT_SIZE = 100;
    private static final Integer MAX_OVERLAPPING_SIZE = 10;

    @PostConstruct
    public void initializeRAGDataSource() {
        initializeRAGCollection();
        importRAGSourceFile();
    }

    private void initializeRAGCollection() {
        qDrantService.createCollection(collectionName, Collections.Distance.Euclid, VECTOR_SIZE);
    }

    private void importRAGSourceFile() {
        Document document = FileSystemDocumentLoader.loadDocument("src/main/resources/rag/boy-who-cries-the-wolf.txt", new TextDocumentParser());

        DocumentByLineSplitter splitter = new DocumentByLineSplitter(MAX_SEGMENT_SIZE, MAX_OVERLAPPING_SIZE);
        List<TextSegment> segments = splitter.split(document);

        List<String> segmentsTexts = segments.stream().map(TextSegment::text).toList();
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(segmentsTexts);

        List<EmbeddingItem> embeddingItems = openAIEmbeddingsService.getEmbeddings(embeddingsOptions);
        log.info("Embeddings for [{}] segments loaded", embeddingItems.size());

        var vectors = convertEmbeddingsToVector(segmentsTexts, embeddingItems);

        this.qDrantService.savePoints(collectionName, vectors);
        log.info("Vectors for [{}] segments saved", vectors.size());
    }

    private List<Points.PointStruct> convertEmbeddingsToVector(List<String> prompts, List<EmbeddingItem> embeddings) {
        return embeddings
                .stream()
                .map(embedding -> {
                    int promptIndex = embedding.getPromptIndex();
                    return mapToPointsStruct(prompts.get(promptIndex), embedding.getEmbedding());
                })
                .toList();
    }

    private Points.PointStruct mapToPointsStruct(String prompt, List<Float> points) {
        return Points.PointStruct.newBuilder()
                .setId(id(UUID.randomUUID()))
                .setVectors(vectors(points))
                .putAllPayload(Map.of("prompt", value(prompt)))
                .build();
    }
}
