package com.whisperline.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whisperline.backend.dto.MessageRequest;
import com.whisperline.backend.dto.MessageResponse;
import com.whisperline.backend.service.GptService;
import com.whisperline.backend.service.ResponseValidator;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private GptService gptService;

    @Autowired
    private ResponseValidator responseValidator;

    @GetMapping("/health")
    public String healthCheck() {
        return "Chat API is running";
    }

    @PostMapping("/message")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        try {
            GptService.GptResponse gptResponse = gptService.callGpt(request.getMessage());
            
            String fullResponse = "Counseling Response: " + gptResponse.getCounselingResponse() + 
                                 "\nRisk Level: " + gptResponse.getRiskLevel();
            
            ResponseValidator.ValidationResult validation = responseValidator.validate(fullResponse);
            
            if (!validation.isValid()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setError("Response validation failed: " + validation.getMessage());
                errorResponse.setTimestamp(timestamp);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            MessageResponse response = new MessageResponse(
                gptResponse.getCounselingResponse(),
                timestamp,
                gptResponse.getRiskLevel()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setError("Failed to process message: " + e.getMessage());
            errorResponse.setTimestamp(timestamp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/status")
    public ApiStatus getStatus() {
        return new ApiStatus("active", "Chat service is ready");
    }

    public static class ApiStatus {
        private String status;
        private String message;

        public ApiStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

