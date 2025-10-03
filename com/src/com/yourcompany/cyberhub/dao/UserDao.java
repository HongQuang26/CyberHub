package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.Admin;
import com.yourcompany.cyberhub.model.Customer;
import com.yourcompany.cyberhub.model.User;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");

                if ("ADMIN".equals(role)) {
                    return new Admin(userId, username, password, fullName);
                } else {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    return new Customer(userId, username, password, fullName, balance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER'";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getBigDecimal("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean addCustomer(String username, String password, String fullName) {
        String sql = "INSERT INTO users (username, password, full_name, role, balance) VALUES (?, ?, ?, 'CUSTOMER', 0)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalance(int userId, BigDecimal newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int userId) {
        // Check if user is CUSTOMER role before deleting
        String checkSql = "SELECT role FROM users WHERE user_id = ?";
        String deleteSql = "DELETE FROM users WHERE user_id = ? AND role = 'CUSTOMER'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && "CUSTOMER".equals(rs.getString("role"))) {
                // User is a customer, proceed with deletion
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, userId);
                    int affectedRows = deleteStmt.executeUpdate();
                    return affectedRows > 0;
                }
            }
            return false; // Not a customer or doesn't exist
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}