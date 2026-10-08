-- ==========================================================
-- Employee Management System - Database Schema
-- Database: MySQL
-- ==========================================================

-- 1. Create Database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS employee_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 2. Switch to the employee_db
USE employee_db;

-- 3. Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    salary DOUBLE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_department (department),
    INDEX idx_name (name),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Insert Sample Employee Data
INSERT INTO employees (name, email, department, designation, salary, phone, hire_date)
VALUES 
    ('Alexander Pierce', 'alexander.pierce@company.com', 'Engineering', 'Senior Software Engineer', 95000.00, '+1-555-0101', '2022-03-15'),
    ('Sophia Montgomery', 'sophia.m@company.com', 'Human Resources', 'HR Manager', 78000.00, '+1-555-0102', '2021-06-01'),
    ('Liam Chen', 'liam.chen@company.com', 'Engineering', 'Tech Lead', 115000.00, '+1-555-0103', '2020-01-10'),
    ('Emma Watson', 'emma.watson@company.com', 'Marketing', 'Marketing Director', 88000.00, '+1-555-0104', '2022-08-20'),
    ('Lucas Rodriguez', 'lucas.r@company.com', 'Finance', 'Financial Analyst', 72000.00, '+1-555-0105', '2023-02-01'),
    ('Olivia Taylor', 'olivia.t@company.com', 'Sales', 'Sales Executive', 65000.00, '+1-555-0106', '2023-05-18'),
    ('Noah Patel', 'noah.patel@company.com', 'Engineering', 'DevOps Engineer', 92000.00, '+1-555-0107', '2022-11-05')
ON DUPLICATE KEY UPDATE name=name;
