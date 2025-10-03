package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.Transaction;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public boolean addTransaction(int userId, BigDecimal amount, String transactionType) {
        String sql = "INSERT INTO transactions (user_id, amount, transaction_type, transaction_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setBigDecimal(2, amount);
            pstmt.setString(3, transactionType);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.username FROM transactions t JOIN users u ON t.user_id = u.user_id ORDER BY transaction_date DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getBigDecimal("amount"),
                        rs.getString("transaction_type"),
                        rs.getTimestamp("transaction_date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}