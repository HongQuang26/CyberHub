package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.ComputerDao;
import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.dao.FoodDao;
import com.yourcompany.cyberhub.model.Computer;
import com.yourcompany.cyberhub.model.Customer;
import com.yourcompany.cyberhub.model.FoodItem;
import com.yourcompany.cyberhub.util.InputValidator;
import com.yourcompany.cyberhub.util.MoneyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

public class AdminDashboardFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel computerPanel;
    private JPanel computerGridPanel; // The actual grid that holds computer panels
    private JPanel customerPanel;
    private JPanel foodMenuPanel;
    private JPanel transactionPanel;

    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTable foodMenuTable;
    private DefaultTableModel foodMenuTableModel;

    private ComputerDao computerDao;
    private UserDao userDao;
    private FoodDao foodDao;

    public AdminDashboardFrame() {
        computerDao = new ComputerDao();
        userDao = new UserDao();
        foodDao = new FoodDao();

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

        // Tab 3: Quản lý thực đơn
        setupFoodMenuPanel();
        tabbedPane.addTab("Quản lý Thực đơn", foodMenuPanel);

        // Tab 4: Thống kê
        transactionPanel = new JPanel(); // Sẽ làm sau
        tabbedPane.addTab("Lịch sử & Thống kê", transactionPanel);

        add(tabbedPane);

        // Tải dữ liệu lần đầu
        refreshComputerStatus();
        refreshCustomerList();
        refreshFoodMenu();
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
            row.add(MoneyFormatter.format(customer.getBalance()));
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
        String currentBalanceStr = (String) customerTableModel.getValueAt(selectedRow, 3);
        
        // Parse current balance from formatted string
        BigDecimal currentBalance;
        try {
            currentBalance = MoneyFormatter.parse(currentBalanceStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc số dư hiện tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog with formatted text field
        JTextField amountField = new JTextField(15);
        MoneyFormatter.applyMoneyFormat(amountField);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nhập số tiền cần nạp cho " + username + ":"));
        panel.add(amountField);
        panel.add(new JLabel("(Số tiền sẽ tự động được định dạng)"));

        int result = JOptionPane.showConfirmDialog(this, panel, "Nạp tiền", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String amountStr = amountField.getText().trim();
            if (!amountStr.isEmpty()) {
                try {
                    BigDecimal amount = MoneyFormatter.parse(amountStr);
                    
                    // Validate amount
                    if (!InputValidator.isValidAmount(amount)) {
                        JOptionPane.showMessageDialog(this, InputValidator.getAmountErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    BigDecimal newBalance = currentBalance.add(amount);
                    boolean success = userDao.updateBalance(userId, newBalance);

                    if (success) {
                        JOptionPane.showMessageDialog(this, "Nạp tiền thành công! Số tiền: " + MoneyFormatter.format(amount) + " VND", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        refreshCustomerList(); // Cập nhật lại bảng
                        // TODO: Ghi lại giao dịch này vào bảng 'transactions'
                    } else {
                        JOptionPane.showMessageDialog(this, "Nạp tiền thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException | ParseException e) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
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

    private void setupFoodMenuPanel() {
        foodMenuPanel = new JPanel(new BorderLayout(10, 10));
        foodMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table for food menu
        foodMenuTableModel = new DefaultTableModel(
            new String[]{"ID", "Tên món", "Giá (VND)", "Danh mục", "Có sẵn"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        foodMenuTable = new JTable(foodMenuTableModel);
        foodMenuTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foodMenuPanel.add(new JScrollPane(foodMenuTable), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm món");
        JButton btnEdit = new JButton("Sửa món");
        JButton btnDelete = new JButton("Xóa món");
        JButton btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        foodMenuPanel.add(buttonPanel, BorderLayout.NORTH);

        // Event handlers
        btnRefresh.addActionListener(e -> refreshFoodMenu());
        btnAdd.addActionListener(e -> showAddFoodDialog());
        btnEdit.addActionListener(e -> showEditFoodDialog());
        btnDelete.addActionListener(e -> showDeleteFoodDialog());
    }

    private void refreshFoodMenu() {
        foodMenuTableModel.setRowCount(0);
        List<FoodItem> foodItems = foodDao.getAllFoodItemsIncludingUnavailable();
        
        for (FoodItem item : foodItems) {
            Object[] row = {
                item.getFoodId(),
                item.getFoodName(),
                MoneyFormatter.format(item.getPrice()),
                item.getCategory(),
                item.isAvailable() ? "Có" : "Không"
            };
            foodMenuTableModel.addRow(row);
        }
    }

    private void showAddFoodDialog() {
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(15);
        MoneyFormatter.applyMoneyFormat(priceField);
        JTextField categoryField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên món:"));
        panel.add(nameField);
        panel.add(new JLabel("Giá (VND):"));
        panel.add(priceField);
        panel.add(new JLabel("(Số tiền sẽ tự động được định dạng)"));
        panel.add(new JLabel("Danh mục:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm món mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String category = categoryField.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = MoneyFormatter.parse(priceStr);
                
                if (!InputValidator.isValidAmount(price)) {
                    JOptionPane.showMessageDialog(this, InputValidator.getAmountErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = foodDao.addFoodItem(name, price, category);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Thêm món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    refreshFoodMenu();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm món thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException | ParseException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditFoodDialog() {
        int selectedRow = foodMenuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int foodId = (int) foodMenuTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) foodMenuTableModel.getValueAt(selectedRow, 1);
        String currentPriceStr = (String) foodMenuTableModel.getValueAt(selectedRow, 2);
        String currentCategory = (String) foodMenuTableModel.getValueAt(selectedRow, 3);
        String currentAvailableStr = (String) foodMenuTableModel.getValueAt(selectedRow, 4);
        boolean currentAvailable = "Có".equals(currentAvailableStr);

        JTextField nameField = new JTextField(currentName, 20);
        JTextField priceField = new JTextField(15);
        MoneyFormatter.applyMoneyFormat(priceField);
        priceField.setText(currentPriceStr);
        JTextField categoryField = new JTextField(currentCategory, 20);
        JCheckBox availableCheckBox = new JCheckBox("Có sẵn", currentAvailable);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên món:"));
        panel.add(nameField);
        panel.add(new JLabel("Giá (VND):"));
        panel.add(priceField);
        panel.add(new JLabel("(Số tiền sẽ tự động được định dạng)"));
        panel.add(new JLabel("Danh mục:"));
        panel.add(categoryField);
        panel.add(availableCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa món",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String category = categoryField.getText().trim();
            boolean available = availableCheckBox.isSelected();

            if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = MoneyFormatter.parse(priceStr);
                
                if (!InputValidator.isValidAmount(price)) {
                    JOptionPane.showMessageDialog(this, InputValidator.getAmountErrorMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = foodDao.updateFoodItem(foodId, name, price, category, available);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Sửa món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    refreshFoodMenu();
                } else {
                    JOptionPane.showMessageDialog(this, "Sửa món thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException | ParseException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteFoodDialog() {
        int selectedRow = foodMenuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int foodId = (int) foodMenuTableModel.getValueAt(selectedRow, 0);
        String foodName = (String) foodMenuTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa món:\n" + foodName + "\n\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = foodDao.deleteFoodItem(foodId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshFoodMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa món thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}