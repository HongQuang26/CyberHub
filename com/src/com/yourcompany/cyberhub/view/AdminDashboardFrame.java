package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.ComputerDao;
import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.Computer;
import com.yourcompany.cyberhub.model.Customer;
import com.yourcompany.cyberhub.util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

public class AdminDashboardFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel computerPanel;
    private JPanel computerGridPanel; // The actual grid that holds computer panels
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
        computerPanel = new JPanel(new BorderLayout());
        
        // Button panel at the top
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnRefresh);
        computerPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Computer grid panel
        computerGridPanel = new JPanel(new GridLayout(0, 5, 10, 10)); // 0 rows, 5 columns
        computerGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(computerGridPanel);
        computerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh action
        btnRefresh.addActionListener(e -> refreshComputerStatus());
    }

    private void setupCustomerPanel() {
        customerPanel = new JPanel(new BorderLayout(10, 10));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bảng hiển thị - set to non-editable
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Tên đăng nhập", "Họ và Tên", "Số dư (VND)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm khách hàng");
        JButton btnTopUp = new JButton("Nạp tiền");
        JButton btnDelete = new JButton("Xóa khách hàng");
        JButton btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnTopUp);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        customerPanel.add(buttonPanel, BorderLayout.NORTH);

        // Xử lý sự kiện cho các nút
        btnRefresh.addActionListener(e -> refreshCustomerList());
        btnAdd.addActionListener(e -> showAddCustomerDialog());
        btnTopUp.addActionListener(e -> showTopUpDialog());
        btnDelete.addActionListener(e -> showDeleteCustomerDialog());
    }

    private void refreshComputerStatus() {
        computerGridPanel.removeAll();
        List<Computer> computers = computerDao.getAllComputers();
        for (Computer computer : computers) {
            JPanel singleComputerPanel = new JPanel(new BorderLayout());
            singleComputerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            singleComputerPanel.setPreferredSize(new Dimension(120, 100));

            JLabel nameLabel = new JLabel(computer.getComputerName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            String statusText = "";
            switch (computer.getStatus()) {
                case "AVAILABLE":
                    statusText = "Sẵn sàng";
                    break;
                case "IN_USE":
                    statusText = "Đang dùng";
                    break;
                case "MAINTENANCE":
                    statusText = "Bảo trì";
                    break;
                default:
                    statusText = computer.getStatus();
            }
            JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);

            singleComputerPanel.add(nameLabel, BorderLayout.CENTER);
            singleComputerPanel.add(statusLabel, BorderLayout.SOUTH);

            // Đặt màu nền dựa trên trạng thái
            switch (computer.getStatus()) {
                case "AVAILABLE":
                    singleComputerPanel.setBackground(new Color(144, 238, 144)); // Light Green
                    statusLabel.setForeground(new Color(0, 100, 0)); // Dark Green
                    break;
                case "IN_USE":
                    singleComputerPanel.setBackground(new Color(255, 182, 193)); // Light Red/Pink
                    statusLabel.setForeground(new Color(139, 0, 0)); // Dark Red
                    break;
                case "MAINTENANCE":
                    singleComputerPanel.setBackground(Color.LIGHT_GRAY);
                    statusLabel.setForeground(Color.DARK_GRAY);
                    break;
            }
            computerGridPanel.add(singleComputerPanel);
        }
        computerGridPanel.revalidate();
        computerGridPanel.repaint();
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
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();

            // Validate inputs
            if (!InputValidator.isValidUsername(username)) {
                JOptionPane.showMessageDialog(this, InputValidator.getUsernameErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!InputValidator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, InputValidator.getPasswordErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!InputValidator.isValidFullName(fullName)) {
                JOptionPane.showMessageDialog(this, InputValidator.getFullNameErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                BigDecimal amount = new BigDecimal(amountStr.trim());
                
                // Validate amount
                if (!InputValidator.isValidAmount(amount)) {
                    JOptionPane.showMessageDialog(this, InputValidator.getAmountErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal newBalance = currentBalance.add(amount);
                boolean success = userDao.updateBalance(userId, newBalance);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Nạp tiền thành công! Số tiền: " + amount + " VND", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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

    private void showDeleteCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) customerTableModel.getValueAt(selectedRow, 0);
        String username = (String) customerTableModel.getValueAt(selectedRow, 1);
        String fullName = (String) customerTableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa khách hàng:\n" +
            "Tên đăng nhập: " + username + "\n" +
            "Họ và tên: " + fullName + "\n\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDao.deleteCustomer(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerList();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}