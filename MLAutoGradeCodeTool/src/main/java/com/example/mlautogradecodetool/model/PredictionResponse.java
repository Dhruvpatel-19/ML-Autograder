package com.example.mlautogradecodetool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionResponse {

	@JsonProperty("predicted_score")
    private float predictedScore;

    public float getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(float predictedScore) {
        this.predictedScore = predictedScore;
    }
}