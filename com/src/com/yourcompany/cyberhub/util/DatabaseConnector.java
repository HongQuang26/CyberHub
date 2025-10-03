package com.yourcompany.cyberhub.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/cyberhub_management";
    private static final String MYSQL_USER = "root";
    // !!! THAY MẬT KHẨU CỦA BẠN VÀO ĐÂY !!!
    private static final String MYSQL_PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }
}