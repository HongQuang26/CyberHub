package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.Session;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class SessionDao {
    public Session startSession(int userId, int computerId) {
        String sql = "INSERT INTO sessions (user_id, computer_id, start_time) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, computerId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return getActiveSessionByComputerId(computerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean endSession(int sessionId, BigDecimal totalCost) {
        String sql = "UPDATE sessions SET end_time = ?, total_cost = ? WHERE session_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setBigDecimal(2, totalCost);
            pstmt.setInt(3, sessionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Session getActiveSessionByComputerId(int computerId) {
        String sql = "SELECT s.*, u.username FROM sessions s JOIN users u ON s.user_id = u.user_id WHERE s.computer_id = ? AND s.end_time IS NULL";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, computerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Session(
                        rs.getInt("session_id"),
                        rs.getInt("user_id"),
                        rs.getInt("computer_id"),
                        rs.getString("username"),
                        rs.getTimestamp("start_time").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}