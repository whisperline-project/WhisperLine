package com.whisperline.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

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
            if (isValid) {
                logger.info("Database connection test: SUCCESS");
                logger.info("Connected to database: {}", url);
            } else {
                logger.warn("Database connection test: FAILED");
            }
            return isValid;
        } catch (SQLException e) {
            logger.error("Database connection test FAILED: {}", e.getMessage());
            return false;
        }
    }
}
