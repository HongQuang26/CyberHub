package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.User;
import com.yourcompany.cyberhub.util.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDao userDao;

    public LoginFrame() {
        userDao = new UserDao();
        setTitle("Đăng nhập Hệ thống Quản lý Quán Net");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Đăng nhập");
        add(new JLabel()); // Placeholder
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate username format
        if (!InputValidator.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this, InputValidator.getUsernameErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userDao.login(username, password);

            if (user != null) {
                if ("ADMIN".equals(user.getRole())) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Chào mừng " + user.getFullName(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Mở màn hình dashboard của Admin
                    new AdminDashboardFrame().setVisible(true);
                    dispose(); // Đóng cửa sổ đăng nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Chức năng đăng nhập cho khách hàng chưa được hỗ trợ.", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng.", 
                    "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra lại cấu hình.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}