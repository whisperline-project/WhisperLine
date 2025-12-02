package com.whisperline.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public boolean testDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            boolean isValid = connection.isValid(5);
            System.out.println("Database connection test: " + (isValid ? "SUCCESS" : "FAILED"));
            if (isValid) {
                System.out.println("Connected to database: " + url);
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("Database connection test FAILED: " + e.getMessage());
            return false;
        }
    }
}

