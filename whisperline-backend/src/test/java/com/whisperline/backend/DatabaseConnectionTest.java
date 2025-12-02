package com.whisperline.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource should not be null");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData, "DatabaseMetaData should not be null");

            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            String url = metaData.getURL();
            String username = metaData.getUserName();

            System.out.println("Database Product Name: " + databaseProductName);
            System.out.println("Database Product Version: " + databaseProductVersion);
            System.out.println("Database URL: " + url);
            System.out.println("Database Username: " + username);

            assertEquals("MySQL", databaseProductName, "Should be connected to MySQL");
            assertTrue(url.contains("WhisperLine"), "URL should contain WhisperLine database name");
        }
    }

    @Test
    public void testDatabaseConnectionValidity() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5);
            assertTrue(isValid, "Connection should be valid");
            System.out.println("Connection validity check: PASSED");
        }
    }
}

