package com.yourcompany.cyberhub.dao;

import com.yourcompany.cyberhub.model.Order;
import com.yourcompany.cyberhub.model.OrderDetail;
import com.yourcompany.cyberhub.util.DatabaseConnector;

import java.sql.*;
import java.util.List;

public class OrderDao {
    public boolean createOrder(Order order, List<OrderDetail> details) {
        String sqlOrder = "INSERT INTO orders (user_id, session_id, order_time, total_amount, is_paid) VALUES (?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, item_id, quantity, price_per_item) VALUES (?, ?, ?, ?)";
        String sqlStockUpdate = "UPDATE menu_items SET storage = storage - ? WHERE item_id = ? AND storage >= ?";
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStockUpdate)) {
                for (OrderDetail detail : details) {
                    pstmtStock.setInt(1, detail.getQuantity());
                    pstmtStock.setInt(2, detail.getItemId());
                    pstmtStock.setInt(3, detail.getQuantity());

                    int rowsAffected = pstmtStock.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new SQLException("Not enough stock for item_id: " + detail.getItemId());
                    }
                }
            }

            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setInt(1, order.getUserId());
                pstmtOrder.setInt(2, order.getSessionId());
                pstmtOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderTime()));
                pstmtOrder.setBigDecimal(4, order.getTotalAmount());
                pstmtOrder.setBoolean(5, order.isPaid());
                pstmtOrder.executeUpdate();

                try (ResultSet generatedKeys = pstmtOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                            for (OrderDetail detail : details) {
                                pstmtDetail.setInt(1, orderId);
                                pstmtDetail.setInt(2, detail.getItemId());
                                pstmtDetail.setInt(3, detail.getQuantity());
                                pstmtDetail.setBigDecimal(4, detail.getPricePerItem());
                                pstmtDetail.addBatch();
                            }
                            pstmtDetail.executeBatch();
                        }
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
