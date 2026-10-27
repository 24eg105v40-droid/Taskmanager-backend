-- =============================================================
-- Daily Task Manager - MySQL Database Schema Script
-- =============================================================

CREATE DATABASE IF NOT EXISTS task_manager_db;
USE task_manager_db;

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed initial tasks for testing and demonstration
INSERT INTO tasks (title, description, due_date, status) VALUES
('Set up Daily Task Manager', 'Initialize Spring Boot backend with MySQL and connect React frontend.', CURDATE(), 'COMPLETED'),
('Review REST API Endpoints', 'Verify GET, POST, PUT, and DELETE operations with input validation.', CURDATE(), 'PENDING'),
('Organize Daily Schedule', 'Add tasks for the upcoming week and set appropriate due dates.', CURDATE() + INTERVAL 2 DAY, 'PENDING');
