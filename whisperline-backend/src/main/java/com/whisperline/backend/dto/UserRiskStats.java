package com.whisperline.backend.dto;

public class UserRiskStats {
    private String username;
    private String name;
    private Double averageRiskLevel;
    private Long messageCount;

    public UserRiskStats() {
    }

    public UserRiskStats(String username, String name, Double averageRiskLevel, Long messageCount) {
        this.username = username;
        this.name = name;
        this.averageRiskLevel = averageRiskLevel;
        this.messageCount = messageCount;
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

    public Double getAverageRiskLevel() {
        return averageRiskLevel;
    }

    public void setAverageRiskLevel(Double averageRiskLevel) {
        this.averageRiskLevel = averageRiskLevel;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }
}

