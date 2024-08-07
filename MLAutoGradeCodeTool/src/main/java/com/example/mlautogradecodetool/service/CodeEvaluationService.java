package com.example.mlautogradecodetool.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.mlautogradecodetool.model.PredictionRequest;
import com.example.mlautogradecodetool.model.PredictionResponse;

@Service
public class CodeEvaluationService {
	
    private final RestTemplate restTemplate;

    public CodeEvaluationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PredictionResponse evaluateCode(PredictionRequest request) {
    	String url = "http://localhost:5000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PredictionRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(url, entity, PredictionResponse.class);
    }
}