package com.whisperline.backend.dto;

public class MessageResponse {
    private String response;
    private String timestamp;

    public MessageResponse() {
    }

    public MessageResponse(String response, String timestamp) {
        this.response = response;
        this.timestamp = timestamp;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

