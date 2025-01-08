package com.epam.training.gen.ai.config.qdrant;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.qdrant.QdrantContainer;

import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class QDrantConfig {

    @Value("${collection-name}")
    private String collectionName;

    private static final Integer VECTOR_SIZE = 1536;

    @Bean
    public QdrantContainer qDrantTestContainer() {
        log.info("Creating testContainer for QDrant vector database...");
        var qDrantContainer = new QdrantContainer("qdrant/qdrant:v1.7.4");
        log.info("TestContainer created. Starting...");
        qDrantContainer.start();
        log.info("TestContainer started. Host: [{}], gRPC port: [{}]", qDrantContainer.getHost(), qDrantContainer.getGrpcPort());
        return qDrantContainer;
    }

    @Bean
    public QdrantClient qdrantClient(QdrantContainer qDrantTestContainer) throws ExecutionException, InterruptedException {
        log.info("Creating QDrant client...");
        var qDrantGrpcClient = QdrantGrpcClient.newBuilder(qDrantTestContainer.getHost(), qDrantTestContainer.getGrpcPort(), false).build();
        var qDrantclient = new QdrantClient(qDrantGrpcClient);
        log.info("QDrant client created. Initializing collection...");
        initializeCollection(qDrantclient);
        return qDrantclient;
    }

    private void initializeCollection(QdrantClient qdrantClient) throws ExecutionException, InterruptedException {
        var collectionVectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Euclid)
                .setSize(VECTOR_SIZE)
                .build();

        qdrantClient.createCollectionAsync(collectionName, collectionVectorParams).get();
        log.info("Collection [{}] initialized", collectionName);
    }
}
