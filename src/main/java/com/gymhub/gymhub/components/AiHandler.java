package com.gymhub.gymhub.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymhub.gymhub.dto.AiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@Component
public class AiHandler {
    @Autowired
    RestTemplate restTemplate;

    public double postDataToLocalHost(AiRequestBody aiRequestBody) {
        String url = "http://localhost:8000/predict"; // Replace with your local endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<AiRequestBody> entity = new HttpEntity<>(aiRequestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        String jsonString = response.getBody();
        double predictionVal = 0;
        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON string
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Retrieve the "prediction" node
            JsonNode predictionNode = rootNode.path("prediction");

            // Access the first element in the "prediction" array
            predictionVal = predictionNode.get(0).get(0).asDouble();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionVal;
        
    }


}
