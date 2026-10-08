package com.ems.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager for MySQL JDBC.
 */
public class DBConnection {

    private static final Properties properties = new Properties();
    private static boolean driverLoaded = false;

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "employee_db";
    private static String username = "root";
    private static String password = "";
    private static String driverClass = "com.mysql.cj.jdbc.Driver";
    private static String params = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

    static {
        loadConfiguration();
        loadDriver();
    }

    private DBConnection() {
        // Private constructor for singleton
    }

    /**
     * Loads database configuration from db.properties if available.
     */
    private static void loadConfiguration() {
        try {
            // 1. Try loading from file in resources folder
            File propFile = new File("resources/db.properties");
            if (propFile.exists()) {
                try (InputStream input = new FileInputStream(propFile)) {
                    properties.load(input);
                }
            } else {
                // 2. Try loading from classpath
                try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (input != null) {
                        properties.load(input);
                    }
                }
            }

            if (!properties.isEmpty()) {
                host = properties.getProperty("db.host", host);
                port = properties.getProperty("db.port", port);
                dbName = properties.getProperty("db.name", dbName);
                username = properties.getProperty("db.username", username);
                password = properties.getProperty("db.password", password);
                driverClass = properties.getProperty("db.driver", driverClass);
                params = properties.getProperty("db.params", params);
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Using default MySQL configuration. (Note: " + e.getMessage() + ")");
        }
    }

    /**
     * Loads the MySQL JDBC Driver class.
     */
    private static void loadDriver() {
        try {
            Class.forName(driverClass);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older driver names if any
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException ex) {
                System.err.println("[DBConnection Error] MySQL JDBC Driver not found in classpath.");
                System.err.println("Please ensure mysql-connector-j-*.jar is placed in the 'lib' directory.");
                driverLoaded = false;
            }
        }
    }

    /**
     * Constructs the full JDBC URL for the employee_db.
     */
    public static String getDatabaseUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + params;
    }

    /**
     * Constructs JDBC URL without the database name (for server-level operations like CREATE DATABASE).
     */
    public static String getServerUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + params;
    }

    /**
     * Gets a connection to the employee database.
     *
     * @return java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        return DriverManager.getConnection(getDatabaseUrl(), username, password);
    }

    /**
     * Gets a connection to the MySQL server directly (root level).
     */
    public static Connection getServerConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        return DriverManager.getConnection(getServerUrl(), username, password);
    }

    /**
     * Tests whether the database connection is currently active and valid.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Getters for configuration display
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }

    /**
     * Updates credentials dynamically (e.g. from UI or CLI prompts if needed).
     */
    public static void setCredentials(String newHost, String newPort, String newDbName, String newUser, String newPass) {
        if (newHost != null && !newHost.isBlank()) host = newHost;
        if (newPort != null && !newPort.isBlank()) port = newPort;
        if (newDbName != null && !newDbName.isBlank()) dbName = newDbName;
        if (newUser != null) username = newUser;
        if (newPass != null) password = newPass;
    }
}
