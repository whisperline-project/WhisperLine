package com.whisperline.backend.service;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ResponseValidator {

    private static final Pattern COUNSELING_RESPONSE_PATTERN = 
        Pattern.compile("(?i)counseling\\s+response\\s*:", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern RISK_LEVEL_PATTERN = 
        Pattern.compile("(?i)risk\\s+level\\s*:", Pattern.CASE_INSENSITIVE);

    public ValidationResult validate(String response) {
        if (response == null || response.isBlank()) {
            return new ValidationResult(false, "Response is empty");
        }

        boolean hasCounselingResponse = COUNSELING_RESPONSE_PATTERN.matcher(response).find();
        boolean hasRiskLevel = RISK_LEVEL_PATTERN.matcher(response).find();

        if (!hasCounselingResponse || !hasRiskLevel) {
            return new ValidationResult(false, 
                "Invalid response format detected - missing required keys. " +
                "Expected format: 'Counseling Response: ...' and 'Risk Level: ...'");
        }

        String[] lines = response.split("\n");
        String counselingValue = null;
        String riskValue = null;

        for (String rawLine : lines) {
            String trimmedLine = rawLine.trim();
            if (COUNSELING_RESPONSE_PATTERN.matcher(trimmedLine).find()) {
                counselingValue = extractValue(trimmedLine, "Counseling Response:");
            } else if (RISK_LEVEL_PATTERN.matcher(trimmedLine).find()) {
                riskValue = extractValue(trimmedLine, "Risk Level:");
            }
        }

        if (counselingValue == null || counselingValue.isEmpty()) {
            return new ValidationResult(false, "Counseling Response value is missing");
        }

        if (riskValue == null || riskValue.isEmpty()) {
            return new ValidationResult(false, "Risk Level value is missing");
        }

        try {
            int riskLevel = Integer.parseInt(riskValue.trim());
            if (riskLevel < 0 || riskLevel > 100) {
                return new ValidationResult(false, "Risk Level must be between 0 and 100");
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Risk Level must be a valid number");
        }

        return new ValidationResult(true, "Valid format");
    }

    private String extractValue(String line, String key) {
        int index = line.toLowerCase(Locale.ROOT).indexOf(key.toLowerCase(Locale.ROOT));
        if (index >= 0) {
            return line.substring(index + key.length()).trim();
        }
        return null;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
