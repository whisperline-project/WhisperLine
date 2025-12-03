package com.whisperline.backend.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GptService {

    private static final Logger logger = LoggerFactory.getLogger(GptService.class);

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

    public GptResponse callGpt(String userMessage, boolean echo) throws GptServiceException {
        if (echo) {
            return new GptResponse("Echo: " + userMessage, 0);
        }

        if (apiKey == null || apiKey.isEmpty()) {
            throw new GptServiceException("OpenAI API key is not configured");
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
        requestBody.put("max_completion_tokens", 4000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            logger.debug("Sending request to GPT API - Model: {}", MODEL);
            logger.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                    OPENAI_API_URL,
                    request,
                    String.class
            );

            logger.debug("GPT API Response Status: {}", response.getStatusCode());
            logger.debug("GPT API Full Response Body: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                String content = extractContentFromResponse(response.getBody());
                
                if (content == null || content.isEmpty()) {
                    throw new GptServiceException("Empty content received from GPT API");
                }
                
                return parseResponse(content);
            } else {
                logger.error("GPT API Error Response: {}", response.getBody());
                throw new GptServiceException("OpenAI API error: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("RestClient error calling GPT API: {}", e.getMessage(), e);
            throw new GptServiceException("Failed to call GPT API: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage(), e);
            throw new GptServiceException("Failed to process JSON: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IO error: {}", e.getMessage(), e);
            throw new GptServiceException("IO error occurred: " + e.getMessage(), e);
        }
    }

    private String extractContentFromResponse(String responseBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        logger.debug("Parsed JSON Node: {}", jsonNode);
        
        JsonNode choicesNode = jsonNode.path("choices");
        logger.debug("Choices node: {}", choicesNode);
        
        if (choicesNode.isArray() && !choicesNode.isEmpty()) {
            JsonNode firstChoice = choicesNode.get(0);
            logger.debug("First choice: {}", firstChoice);
            
            JsonNode messageNode = firstChoice.path("message");
            logger.debug("Message node: {}", messageNode);
            
            JsonNode contentNode = messageNode.path("content");
            logger.debug("Content node: {}, isNull: {}, isMissing: {}", 
                    contentNode, contentNode.isNull(), contentNode.isMissingNode());
            
            String content = contentNode.asText();
            logger.debug("Extracted content: [{}], length: {}", content, content.length());
            
            return content;
        }
        
        throw new GptServiceException("No choices in GPT response");
    }

    private GptResponse parseResponse(String rawResponse) throws GptServiceException {
        logger.debug("Parsing GPT Response - Raw: {}", rawResponse);
        String[] lines = rawResponse.split("\n");
        String counselingResponse = null;
        Integer riskLevel = null;

        for (String rawLine : lines) {
            String trimmedLine = rawLine.trim();
            logger.debug("Processing line: {}", trimmedLine);
            
            if (trimmedLine.toLowerCase(Locale.ROOT).startsWith("counseling response:")) {
                counselingResponse = trimmedLine.substring("Counseling Response:".length()).trim();
                logger.debug("Found Counseling Response: {}", counselingResponse);
            } else if (trimmedLine.toLowerCase(Locale.ROOT).startsWith("risk level:")) {
                try {
                    String riskStr = trimmedLine.substring("Risk Level:".length()).trim();
                    riskLevel = Integer.valueOf(riskStr);
                    logger.debug("Found Risk Level: {}", riskLevel);
                } catch (NumberFormatException e) {
                    logger.error("Error parsing risk level: {}", e.getMessage());
                    throw new GptServiceException("Invalid risk level format", e);
                }
            }
        }

        if (counselingResponse == null || riskLevel == null) {
            logger.warn("Validation failed - Counseling Response: {}, Risk Level: {}", counselingResponse, riskLevel);
            throw new GptServiceException("Invalid response format detected - possible jailbreak attempt");
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

    public static class GptServiceException extends RuntimeException {
        public GptServiceException(String message) {
            super(message);
        }

        public GptServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
