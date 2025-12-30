package com.amazon.automation.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseUtil {
    private static final Logger logger = LogManager.getLogger(DatabaseUtil.class);
    private static final String DB_URL = "jdbc:sqlite:amazon.db";
    private static Connection connection;

    public static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
        }
    }

    private static void createTables() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS websites(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "url TEXT NOT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            logger.info("Websites table created/verified");
        }
    }

    public static void insertOrUpdateWebsite(String name, String url) {
        String sql = "INSERT OR REPLACE INTO websites(name, url) VALUES(?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, url);
            pstmt.executeUpdate();
            logger.info("Website inserted/updated: {} - {}", name, url);
        } catch (SQLException e) {
            logger.error("Failed to insert/update website", e);
        }
    }

    public static String getWebsiteUrl(String name) {
        String sql = "SELECT url FROM websites WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String url = rs.getString("url");
                logger.info("Retrieved URL for {}: {}", name, url);
                return url;
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve website URL", e);
        }
        
        logger.warn("No URL found for website: {}", name);
        return null;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Failed to close database connection", e);
        }
    }
}