package com.epam.training.gen.ai.service.QDrant;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QDrantService {

    private final QdrantClient qdrantClient;

    @SneakyThrows
    public void createCollection(String collectionName, Collections.Distance distance, int vectorSize) {
        var collectionVectorParams = Collections.VectorParams.newBuilder()
                .setDistance(distance)
                .setSize(vectorSize)
                .build();

            qdrantClient.createCollectionAsync(collectionName, collectionVectorParams).get();
            log.info("Collection [{}] initialized", collectionName);
    }

    @SneakyThrows
    public List<Points.ScoredPoint> searchEmbeddings(Points.SearchPoints searchPoints) {
        return qdrantClient.searchAsync(searchPoints).get();
    }

    @SneakyThrows
    public Points.UpdateResult savePoints(String collectionName, List<Points.PointStruct> vector) {
        return qdrantClient.upsertAsync(collectionName, vector).get();
    }
}
