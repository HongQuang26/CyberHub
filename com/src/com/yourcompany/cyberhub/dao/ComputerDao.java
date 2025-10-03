package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.Computer;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComputerDao {
    public List<Computer> getAllComputers() {
        List<Computer> computers = new ArrayList<>();
        String sql = "SELECT * FROM computers ORDER BY computer_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                computers.add(new Computer(
                        rs.getInt("computer_id"),
                        rs.getString("computer_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return computers;
    }

    public boolean updateComputerStatus(int computerId, String status) {
        String sql = "UPDATE computers SET status = ? WHERE computer_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, computerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}