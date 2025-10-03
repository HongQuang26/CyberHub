package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.User;

import javax.swing.*;
import java.awt.GridLayout;
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
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Đăng nhập");
        add(new JLabel());
        add(loginButton);

        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDao.login(username, password);

        if (user != null) {
            if ("ADMIN".equals(user.getRole())) {
                new AdminDashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Chức năng đăng nhập cho khách hàng chưa được hỗ trợ ở máy chủ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng.", "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
}