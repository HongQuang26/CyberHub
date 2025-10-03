package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.ComputerDao;
import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.Computer;
import com.yourcompany.cyberhub.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

public class AdminDashboardFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel computerPanel;
    private JPanel customerPanel;
    private JPanel transactionPanel;

    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    private ComputerDao computerDao;
    private UserDao userDao;

    public AdminDashboardFrame() {
        computerDao = new ComputerDao();
        userDao = new UserDao();

        setTitle("Bảng điều khiển của Admin");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Tab 1: Quản lý máy tính
        setupComputerPanel();
        tabbedPane.addTab("Quản lý Máy tính", computerPanel);

        // Tab 2: Quản lý khách hàng
        setupCustomerPanel();
        tabbedPane.addTab("Quản lý Khách hàng", customerPanel);

        // Tab 3: Thống kê
        transactionPanel = new JPanel(); // Sẽ làm sau
        tabbedPane.addTab("Lịch sử & Thống kê", transactionPanel);

        add(tabbedPane);

        // Tải dữ liệu lần đầu
        refreshComputerStatus();
        refreshCustomerList();
    }

    private void setupComputerPanel() {
        computerPanel = new JPanel(new GridLayout(0, 5, 10, 10)); // 0 rows, 5 columns
        computerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupCustomerPanel() {
        customerPanel = new JPanel(new BorderLayout(10, 10));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bảng hiển thị
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Tên đăng nhập", "Họ và Tên", "Số dư (VND)"}, 0);
        customerTable = new JTable(customerTableModel);
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm khách hàng");
        JButton btnTopUp = new JButton("Nạp tiền");
        JButton btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnTopUp);
        buttonPanel.add(btnRefresh);
        customerPanel.add(buttonPanel, BorderLayout.NORTH);

        // Xử lý sự kiện cho các nút
        btnRefresh.addActionListener(e -> refreshCustomerList());
        btnAdd.addActionListener(e -> showAddCustomerDialog());
        btnTopUp.addActionListener(e -> showTopUpDialog());
    }

    private void refreshComputerStatus() {
        computerPanel.removeAll();
        List<Computer> computers = computerDao.getAllComputers();
        for (Computer computer : computers) {
            JPanel singleComputerPanel = new JPanel(new BorderLayout());
            singleComputerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel nameLabel = new JLabel(computer.getComputerName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel statusLabel = new JLabel("Trạng thái: " + computer.getStatus(), SwingConstants.CENTER);

            singleComputerPanel.add(nameLabel, BorderLayout.CENTER);
            singleComputerPanel.add(statusLabel, BorderLayout.SOUTH);

            // Đặt màu nền dựa trên trạng thái
            switch (computer.getStatus()) {
                case "AVAILABLE":
                    singleComputerPanel.setBackground(new Color(144, 238, 144)); // Light Green
                    break;
                case "IN_USE":
                    singleComputerPanel.setBackground(new Color(255, 182, 193)); // Light Red/Pink
                    break;
                case "MAINTENANCE":
                    singleComputerPanel.setBackground(Color.LIGHT_GRAY);
                    break;
            }
            computerPanel.add(singleComputerPanel);
        }
        computerPanel.revalidate();
        computerPanel.repaint();
    }

    private void refreshCustomerList() {
        // Xóa dữ liệu cũ
        customerTableModel.setRowCount(0);
        List<Customer> customers = userDao.getAllCustomers();
        for (Customer customer : customers) {
            Vector<Object> row = new Vector<>();
            row.add(customer.getUserId());
            row.add(customer.getUsername());
            row.add(customer.getFullName());
            row.add(customer.getBalance());
            customerTableModel.addRow(row);
        }
    }

    private void showAddCustomerDialog() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField fullNameField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(usernameField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);
        panel.add(new JLabel("Họ và Tên:"));
        panel.add(fullNameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm khách hàng mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = userDao.addCustomer(username, password, fullName);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                refreshCustomerList();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại (Tên đăng nhập có thể đã tồn tại).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTopUpDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để nạp tiền.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) customerTableModel.getValueAt(selectedRow, 0);
        String username = (String) customerTableModel.getValueAt(selectedRow, 1);
        BigDecimal currentBalance = (BigDecimal) customerTableModel.getValueAt(selectedRow, 3);

        String amountStr = JOptionPane.showInputDialog(this, "Nhập số tiền cần nạp cho " + username + ":", "Nạp tiền", JOptionPane.PLAIN_MESSAGE);

        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền phải là số dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal newBalance = currentBalance.add(amount);
                boolean success = userDao.updateBalance(userId, newBalance);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Nạp tiền thành công!");
                    refreshCustomerList(); // Cập nhật lại bảng
                    // TODO: Ghi lại giao dịch này vào bảng 'transactions'
                } else {
                    JOptionPane.showMessageDialog(this, "Nạp tiền thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}