package com.whisperline.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whisperline.backend.dto.MessageRequest;
import com.whisperline.backend.dto.MessageResponse;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Chat API is running";
    }

    @PostMapping("/message")
    public MessageResponse sendMessage(@RequestBody MessageRequest request) {
        // Placeholder response - GPT API integration will be added later
        String responseText = "Echo: " + request.getMessage();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return new MessageResponse(responseText, timestamp);
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

