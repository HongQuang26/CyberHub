package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.FoodItem;
import com.yourcompany.cyberhub.model.FoodOrder;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDao {

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        String sql = "SELECT * FROM food_menu WHERE available = TRUE ORDER BY category, food_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                foodItems.add(new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("food_name"),
                        rs.getBigDecimal("price"),
                        rs.getString("category"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foodItems;
    }

    public List<FoodItem> getAllFoodItemsIncludingUnavailable() {
        List<FoodItem> foodItems = new ArrayList<>();
        String sql = "SELECT * FROM food_menu ORDER BY category, food_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                foodItems.add(new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("food_name"),
                        rs.getBigDecimal("price"),
                        rs.getString("category"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foodItems;
    }

    public boolean addFoodItem(String foodName, BigDecimal price, String category) {
        String sql = "INSERT INTO food_menu (food_name, price, category, available) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, foodName);
            pstmt.setBigDecimal(2, price);
            pstmt.setString(3, category);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFoodItem(int foodId, String foodName, BigDecimal price, String category, boolean available) {
        String sql = "UPDATE food_menu SET food_name = ?, price = ?, category = ?, available = ? WHERE food_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, foodName);
            pstmt.setBigDecimal(2, price);
            pstmt.setString(3, category);
            pstmt.setBoolean(4, available);
            pstmt.setInt(5, foodId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFoodItem(int foodId) {
        String sql = "DELETE FROM food_menu WHERE food_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createFoodOrder(int userId, int foodId, int quantity, BigDecimal totalPrice) {
        String sql = "INSERT INTO food_orders (user_id, food_id, quantity, total_price, status) VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, foodId);
            pstmt.setInt(3, quantity);
            pstmt.setBigDecimal(4, totalPrice);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FoodOrder> getOrdersByUser(int userId) {
        List<FoodOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM food_orders WHERE user_id = ? ORDER BY order_time DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(new FoodOrder(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getInt("food_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("order_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
