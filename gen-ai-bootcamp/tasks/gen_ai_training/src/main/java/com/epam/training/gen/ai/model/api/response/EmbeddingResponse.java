package com.epam.training.gen.ai.model.api.response;

import io.qdrant.client.grpc.Points;

public record EmbeddingResponse(String id, Float score) {

    public static EmbeddingResponse fromScoredPoints(Points.ScoredPoint scoredPoint) {
        return new EmbeddingResponse(scoredPoint.getId().getUuid(), scoredPoint.getScore());
    }

}
