package com.ems.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility responsible for auto-initializing the database, tables, and demo seed data.
 */
public class DatabaseInitializer {

    /**
     * Initializes the MySQL database and table schema.
     *
     * @return true if initialization succeeded or database is ready, false otherwise.
     */
    public static boolean initializeDatabase() {
        // Step 1: Attempt to create database if not exists
        try (Connection serverConn = DBConnection.getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + DBConnection.getDbName() +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDbSql);

        } catch (SQLException e) {
            // It's possible the user doesn't have permission to connect without DB, or DB already exists.
            // We will continue to try connecting directly to the DB.
        }

        // Step 2: Create the table inside employee_db
        try (Connection dbConn = DBConnection.getConnection();
             Statement stmt = dbConn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "department VARCHAR(50) NOT NULL, " +
                    "designation VARCHAR(50) NOT NULL, " +
                    "salary DOUBLE NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "hire_date DATE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "INDEX idx_dept (department), " +
                    "INDEX idx_name (name)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            stmt.executeUpdate(createTableSql);

            // Step 3: Check if table is empty and seed demo data if needed
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM employees")) {
                if (rs.next() && rs.getInt("count") == 0) {
                    seedDemoData(stmt);
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println(AnsiColors.boldRed("\n[Database Initialization Error] Could not connect to MySQL: " + e.getMessage()));
            System.err.println(AnsiColors.yellow("Please ensure:"));
            System.err.println("  1. MySQL server / XAMPP is running on port " + DBConnection.getPort());
            System.err.println("  2. Username/Password in 'resources/db.properties' are correct (current user: '" + DBConnection.getUsername() + "')\n");
            return false;
        }
    }

    private static void seedDemoData(Statement stmt) {
        try {
            String seedSql = "INSERT INTO employees (name, email, department, designation, salary, phone, hire_date) VALUES " +
                    "('Alexander Pierce', 'alexander.pierce@company.com', 'Engineering', 'Senior Software Engineer', 95000.00, '+1-555-0101', '2022-03-15'), " +
                    "('Sophia Montgomery', 'sophia.m@company.com', 'Human Resources', 'HR Manager', 78000.00, '+1-555-0102', '2021-06-01'), " +
                    "('Liam Chen', 'liam.chen@company.com', 'Engineering', 'Tech Lead', 115000.00, '+1-555-0103', '2020-01-10'), " +
                    "('Emma Watson', 'emma.watson@company.com', 'Marketing', 'Marketing Director', 88000.00, '+1-555-0104', '2022-08-20'), " +
                    "('Lucas Rodriguez', 'lucas.r@company.com', 'Finance', 'Financial Analyst', 72000.00, '+1-555-0105', '2023-02-01'), " +
                    "('Olivia Taylor', 'olivia.t@company.com', 'Sales', 'Sales Executive', 65000.00, '+1-555-0106', '2023-05-18'), " +
                    "('Noah Patel', 'noah.patel@company.com', 'Engineering', 'DevOps Engineer', 92000.00, '+1-555-0107', '2022-11-05')";

            stmt.executeUpdate(seedSql);
            System.out.println(AnsiColors.green("[DatabaseInitializer] Sample employee dataset seeded successfully."));
        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Note: Could not insert demo data (" + e.getMessage() + ")");
        }
    }
}
