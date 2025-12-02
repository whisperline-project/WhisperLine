package com.whisperline.backend.dto;

import java.time.LocalDateTime;

public class RiskAlert {
    private String username;
    private String name;
    private String message;
    private Integer riskLevel;
    private LocalDateTime timestamp;

    public RiskAlert() {
    }

    public RiskAlert(String username, String name, String message, Integer riskLevel, LocalDateTime timestamp) {
        this.username = username;
        this.name = name;
        this.message = message;
        this.riskLevel = riskLevel;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

