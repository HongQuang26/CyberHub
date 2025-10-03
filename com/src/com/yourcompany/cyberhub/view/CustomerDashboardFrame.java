package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.ComputerDao;
import com.yourcompany.cyberhub.dao.FoodDao;
import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.Computer;
import com.yourcompany.cyberhub.model.Customer;
import com.yourcompany.cyberhub.model.FoodItem;
import com.yourcompany.cyberhub.util.MoneyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CustomerDashboardFrame extends JFrame {
    
    private Customer customer;
    private ComputerDao computerDao;
    private FoodDao foodDao;
    private UserDao userDao;
    
    private JTabbedPane tabbedPane;
    private JPanel computerPanel;
    private JPanel foodPanel;
    private JPanel accountPanel;
    
    private JPanel computerGridPanel;
    private JTable foodTable;
    private DefaultTableModel foodTableModel;
    
    private JLabel balanceLabel;
    private JLabel usernameLabel;
    
    public CustomerDashboardFrame(Customer customer) {
        this.customer = customer;
        this.computerDao = new ComputerDao();
        this.foodDao = new FoodDao();
        this.userDao = new UserDao();
        
        setTitle("Quán Net - " + customer.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Chọn máy
        setupComputerPanel();
        tabbedPane.addTab("Chọn máy tính", computerPanel);
        
        // Tab 2: Đặt đồ ăn
        setupFoodPanel();
        tabbedPane.addTab("Đặt đồ ăn", foodPanel);
        
        // Tab 3: Tài khoản
        setupAccountPanel();
        tabbedPane.addTab("Tài khoản", accountPanel);
        
        add(tabbedPane);
        
        // Load initial data
        refreshComputerStatus();
        refreshFoodMenu();
        refreshAccountInfo();
    }
    
    private void setupComputerPanel() {
        computerPanel = new JPanel(new BorderLayout());
        
        // Info panel at top
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Chọn máy tính có sẵn để sử dụng"));
        JButton btnRefresh = new JButton("Làm mới");
        infoPanel.add(btnRefresh);
        computerPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Computer grid
        computerGridPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        computerGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(computerGridPanel);
        computerPanel.add(scrollPane, BorderLayout.CENTER);
        
        btnRefresh.addActionListener(e -> refreshComputerStatus());
    }
    
    private void setupFoodPanel() {
        foodPanel = new JPanel(new BorderLayout(10, 10));
        foodPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table for food menu
        foodTableModel = new DefaultTableModel(
            new String[]{"ID", "Tên món", "Giá (VND)", "Danh mục"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        foodTable = new JTable(foodTableModel);
        foodTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foodPanel.add(new JScrollPane(foodTable), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnOrder = new JButton("Đặt món");
        JButton btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnOrder);
        buttonPanel.add(btnRefresh);
        foodPanel.add(buttonPanel, BorderLayout.NORTH);
        
        btnOrder.addActionListener(e -> showOrderDialog());
        btnRefresh.addActionListener(e -> refreshFoodMenu());
    }
    
    private void setupAccountPanel() {
        accountPanel = new JPanel(new BorderLayout());
        accountPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        usernameLabel = new JLabel();
        balanceLabel = new JLabel();
        
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        infoPanel.add(usernameLabel);
        infoPanel.add(balanceLabel);
        
        accountPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Logout button
        JButton btnLogout = new JButton("Đăng xuất");
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.add(btnLogout);
        accountPanel.add(logoutPanel, BorderLayout.SOUTH);
        
        btnLogout.addActionListener(e -> logout());
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
            
            // Color based on status
            switch (computer.getStatus()) {
                case "AVAILABLE":
                    singleComputerPanel.setBackground(new Color(144, 238, 144));
                    statusLabel.setForeground(new Color(0, 100, 0));
                    // Make clickable
                    singleComputerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    singleComputerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            selectComputer(computer);
                        }
                    });
                    break;
                case "IN_USE":
                    singleComputerPanel.setBackground(new Color(255, 182, 193));
                    statusLabel.setForeground(new Color(139, 0, 0));
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
    
    private void refreshFoodMenu() {
        foodTableModel.setRowCount(0);
        List<FoodItem> foodItems = foodDao.getAllFoodItems();
        
        for (FoodItem item : foodItems) {
            Object[] row = {
                item.getFoodId(),
                item.getFoodName(),
                MoneyFormatter.format(item.getPrice()),
                item.getCategory()
            };
            foodTableModel.addRow(row);
        }
    }
    
    private void refreshAccountInfo() {
        // Refresh customer data from database
        List<com.yourcompany.cyberhub.model.Customer> customers = userDao.getAllCustomers();
        for (com.yourcompany.cyberhub.model.Customer c : customers) {
            if (c.getUserId() == customer.getUserId()) {
                customer = c;
                break;
            }
        }
        
        usernameLabel.setText("Tên đăng nhập: " + customer.getUsername());
        balanceLabel.setText("Số dư: " + MoneyFormatter.format(customer.getBalance()) + " VND");
    }
    
    private void selectComputer(Computer computer) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn muốn sử dụng " + computer.getComputerName() + "?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = computerDao.updateComputerStatus(computer.getComputerId(), "IN_USE");
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Đã chọn " + computer.getComputerName() + " thành công!\nChúc bạn có trải nghiệm tốt!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshComputerStatus();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể chọn máy này. Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showOrderDialog() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn món ăn từ bảng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int foodId = (int) foodTableModel.getValueAt(selectedRow, 0);
        String foodName = (String) foodTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) foodTableModel.getValueAt(selectedRow, 2);
        
        // Parse price
        BigDecimal price;
        try {
            price = MoneyFormatter.parse(priceStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc giá món ăn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Ask for quantity
        String quantityStr = JOptionPane.showInputDialog(this,
            "Nhập số lượng cho " + foodName + ":",
            "Số lượng",
            JOptionPane.PLAIN_MESSAGE);
        
        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                BigDecimal totalPrice = price.multiply(new BigDecimal(quantity));
                
                // Check balance
                if (customer.getBalance().compareTo(totalPrice) < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Số dư không đủ!\nTổng tiền: " + MoneyFormatter.format(totalPrice) + " VND\nSố dư hiện tại: " + MoneyFormatter.format(customer.getBalance()) + " VND",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Confirm order
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận đặt món:\n" +
                    "Món: " + foodName + "\n" +
                    "Số lượng: " + quantity + "\n" +
                    "Tổng tiền: " + MoneyFormatter.format(totalPrice) + " VND",
                    "Xác nhận đặt món",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Create order
                    boolean orderSuccess = foodDao.createFoodOrder(customer.getUserId(), foodId, quantity, totalPrice);
                    
                    if (orderSuccess) {
                        // Deduct balance
                        BigDecimal newBalance = customer.getBalance().subtract(totalPrice);
                        boolean balanceSuccess = userDao.updateBalance(customer.getUserId(), newBalance);
                        
                        if (balanceSuccess) {
                            JOptionPane.showMessageDialog(this,
                                "Đặt món thành công!\nMón ăn sẽ được giao đến bạn sớm.",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshAccountInfo();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Đặt món thành công nhưng không thể cập nhật số dư.",
                                "Cảnh báo",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Đặt món thất bại. Vui lòng thử lại.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
