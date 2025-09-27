package com.yourcompany.netcafe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // !!! THAY ĐỔI CÁC THÔNG SỐ NÀY CHO PHÙ HỢP VỚI MÁY CỦA BẠN !!!
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/net_cafe_management";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "root"; // <-- THAY MẬT KHẨU CỦA BẠN VÀO ĐÂY

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Xử lý lỗi nếu không tìm thấy driver
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }
}