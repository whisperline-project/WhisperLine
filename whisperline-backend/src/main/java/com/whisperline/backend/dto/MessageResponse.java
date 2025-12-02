package com.whisperline.backend.dto;

public class MessageResponse {
    private String response;
    private String timestamp;
    private Integer riskLevel;
    private String error;

    public MessageResponse() {
    }

    public MessageResponse(String response, String timestamp) {
        this.response = response;
        this.timestamp = timestamp;
    }

    public MessageResponse(String response, String timestamp, Integer riskLevel) {
        this.response = response;
        this.timestamp = timestamp;
        this.riskLevel = riskLevel;
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

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

