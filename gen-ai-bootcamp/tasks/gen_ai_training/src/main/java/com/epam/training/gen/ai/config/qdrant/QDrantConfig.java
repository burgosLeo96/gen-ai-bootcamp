package com.epam.training.gen.ai.config.qdrant;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.qdrant.QdrantContainer;

@Slf4j
@Configuration
public class QDrantConfig {

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
    public QdrantClient qdrantClient(QdrantContainer qDrantTestContainer) {
        log.info("Creating QDrant client...");
        var qDrantGrpcClient = QdrantGrpcClient.newBuilder(qDrantTestContainer.getHost(), qDrantTestContainer.getGrpcPort(), false).build();
        var qDrantclient = new QdrantClient(qDrantGrpcClient);
        log.info("QDrant client created. Initializing collection...");
        return qDrantclient;
    }
}
