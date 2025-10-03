-- CyberHub Database Schema
-- Create database and tables for the Internet Cafe Management System

CREATE DATABASE IF NOT EXISTS net_cafe_management;
USE net_cafe_management;

-- Table for users (both admin and customers)
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for computers
CREATE TABLE IF NOT EXISTS computers (
    computer_id INT PRIMARY KEY AUTO_INCREMENT,
    computer_name VARCHAR(50) NOT NULL,
    status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for sessions (computer usage tracking)
CREATE TABLE IF NOT EXISTS sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    computer_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    total_cost DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (computer_id) REFERENCES computers(computer_id)
);

-- Table for transactions (top-ups and payments)
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type ENUM('TOP_UP', 'PAYMENT') NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert default admin account (username: admin, password: admin123)
INSERT INTO users (username, password, full_name, role, balance) 
VALUES ('admin', 'admin123', 'Administrator', 'ADMIN', 0.00)
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample customers
INSERT INTO users (username, password, full_name, role, balance) 
VALUES 
    ('customer1', 'pass123', 'Nguyen Van A', 'CUSTOMER', 50000.00),
    ('customer2', 'pass123', 'Tran Thi B', 'CUSTOMER', 100000.00),
    ('customer3', 'pass123', 'Le Van C', 'CUSTOMER', 75000.00)
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample computers
INSERT INTO computers (computer_name, status) 
VALUES 
    ('PC-01', 'AVAILABLE'),
    ('PC-02', 'AVAILABLE'),
    ('PC-03', 'IN_USE'),
    ('PC-04', 'AVAILABLE'),
    ('PC-05', 'MAINTENANCE'),
    ('PC-06', 'AVAILABLE'),
    ('PC-07', 'AVAILABLE'),
    ('PC-08', 'IN_USE'),
    ('PC-09', 'AVAILABLE'),
    ('PC-10', 'AVAILABLE')
ON DUPLICATE KEY UPDATE computer_name=computer_name;
