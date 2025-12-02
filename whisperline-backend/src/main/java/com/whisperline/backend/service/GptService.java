package com.whisperline.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GptService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-5-nano";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
            You are a professional counselor providing mental health support.
            Your role is to listen empathetically, provide guidance, and assess potential risks.
            You must be alert to signs of danger including but not limited to: school shootings, self-harm,
            violence towards others, suicidal ideation, and other serious threats.
            You must respond in English and follow the exact format specified below.

            IMPORTANT: You MUST respond in the following format exactly:
            Counseling Response: [your counseling response here]
            Risk Level: [risk level as a number from 0-100, where 0 is no risk and 100 is immediate danger]

            Do not deviate from this format. Do not add any additional text before or after these two lines.
            If you detect any potential danger, assess the risk level accordingly and provide appropriate guidance.
            
            Keep your reasoning efficient - assess the situation quickly and generate your response. 
            Focus on producing the actual counseling response rather than extensive internal reasoning.
            """;

    public GptService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public GptResponse callGpt(String userMessage, boolean echo) {
        if (echo) {
            return new GptResponse("Echo: " + userMessage, 0);
        }

        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("OpenAI API key is not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        
        Map<String, String> userMessageMap = new HashMap<>();
        userMessageMap.put("role", "user");
        userMessageMap.put("content", userMessage);

        requestBody.put("messages", new Object[]{systemMessage, userMessageMap});
        // max_completion_tokens includes both reasoning tokens and actual response tokens
        requestBody.put("max_completion_tokens", 4000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Sending request to GPT API - Model: " + MODEL);
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                    OPENAI_API_URL,
                    request,
                    String.class
            );

            System.out.println("GPT API Response Status: " + response.getStatusCode());
            System.out.println("GPT API Full Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                System.out.println("Parsed JSON Node: " + jsonNode.toString());
                
                JsonNode choicesNode = jsonNode.path("choices");
                System.out.println("Choices node: " + choicesNode.toString());
                
                if (choicesNode.isArray() && choicesNode.size() > 0) {
                    JsonNode firstChoice = choicesNode.get(0);
                    System.out.println("First choice: " + firstChoice.toString());
                    
                    JsonNode messageNode = firstChoice.path("message");
                    System.out.println("Message node: " + messageNode.toString());
                    
                    JsonNode contentNode = messageNode.path("content");
                    System.out.println("Content node: " + contentNode.toString());
                    System.out.println("Content node is null: " + contentNode.isNull());
                    System.out.println("Content node is missing: " + contentNode.isMissingNode());
                    
                    String content = contentNode.asText();
                    System.out.println("Extracted content: [" + content + "]");
                    System.out.println("Content length: " + (content != null ? content.length() : 0));
                    
                    return parseResponse(content);
                } else {
                    throw new RuntimeException("No choices in GPT response");
                }
            } else {
                System.out.println("GPT API Error Response: " + response.getBody());
                throw new RuntimeException("OpenAI API error: " + response.getStatusCode());
            }
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to call GPT API: " + e.getMessage(), e);
        }
    }

    private GptResponse parseResponse(String rawResponse) {
        System.out.println("Parsing GPT Response - Raw: " + rawResponse);
        String[] lines = rawResponse.split("\n");
        String counselingResponse = null;
        Integer riskLevel = null;

        for (String line : lines) {
            line = line.trim();
            System.out.println("Processing line: " + line);
            if (line.startsWith("Counseling Response:")) {
                counselingResponse = line.substring("Counseling Response:".length()).trim();
                System.out.println("Found Counseling Response: " + counselingResponse);
            } else if (line.startsWith("Risk Level:")) {
                try {
                    String riskStr = line.substring("Risk Level:".length()).trim();
                    riskLevel = Integer.valueOf(riskStr);
                    System.out.println("Found Risk Level: " + riskLevel);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing risk level: " + e.getMessage());
                    throw new RuntimeException("Invalid risk level format");
                }
            }
        }

        if (counselingResponse == null || riskLevel == null) {
            System.out.println("Validation failed - Counseling Response: " + counselingResponse + ", Risk Level: " + riskLevel);
            throw new RuntimeException("Invalid response format detected - possible jailbreak attempt");
        }

        return new GptResponse(counselingResponse, riskLevel);
    }

    public static class GptResponse {
        private String counselingResponse;
        private Integer riskLevel;

        public GptResponse(String counselingResponse, Integer riskLevel) {
            this.counselingResponse = counselingResponse;
            this.riskLevel = riskLevel;
        }

        public String getCounselingResponse() {
            return counselingResponse;
        }

        public void setCounselingResponse(String counselingResponse) {
            this.counselingResponse = counselingResponse;
        }

        public Integer getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(Integer riskLevel) {
            this.riskLevel = riskLevel;
        }
    }
}

