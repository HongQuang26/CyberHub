package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.*;
import com.yourcompany.cyberhub.model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class AdminDashboardFrame extends JFrame {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("10000");
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private JTabbedPane tabbedPane;
    private JPanel computerPanel, customerPanel, transactionPanel, systemPanel;
    private JTable customerTable, transactionTable, menuTable;
    private DefaultTableModel customerTableModel, transactionTableModel, menuTableModel;

    private ComputerDao computerDao;
    private UserDao userDao;
    private SessionDao sessionDao;
    private TransactionDao transactionDao;
    private MenuItemDao menuItemDao;

    public AdminDashboardFrame() {
        computerDao = new ComputerDao();
        userDao = new UserDao();
        sessionDao = new SessionDao();
        transactionDao = new TransactionDao();
        menuItemDao = new MenuItemDao();

        setTitle("Bảng điều khiển của Admin - CyberHub");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        setupComputerPanel();
        setupCustomerPanel();
        setupTransactionPanel();
        setupSystemPanel();

        tabbedPane.addTab("💻 Quản lý Máy tính", computerPanel);
        tabbedPane.addTab("👥 Quản lý Khách hàng", customerPanel);
        tabbedPane.addTab("📊 Lịch sử & Thống kê", transactionPanel);
        tabbedPane.addTab("⚙️ Hệ thống", systemPanel);

        add(tabbedPane);
        refreshAll();
    }

    public void refreshAll() {
        refreshComputerStatus();
        refreshCustomerList();
        refreshTransactionList();
        refreshMenuTable();
    }

    private void setupComputerPanel() {
        computerPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        computerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void setupCustomerPanel() {
        customerPanel = new JPanel(new BorderLayout(10, 10));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Tên đăng nhập", "Họ và Tên", "Số dư"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        customerTable = new JTable(customerTableModel);
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm khách hàng");
        JButton btnTopUp = new JButton("Nạp tiền");
        JButton btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnTopUp);
        buttonPanel.add(btnRefresh);
        customerPanel.add(buttonPanel, BorderLayout.NORTH);
        btnRefresh.addActionListener(e -> refreshCustomerList());
        btnAdd.addActionListener(e -> showAddCustomerDialog());
        btnTopUp.addActionListener(e -> showTopUpDialog());
    }

    private void setupTransactionPanel() {
        transactionPanel = new JPanel(new BorderLayout(10, 10));
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        transactionTableModel = new DefaultTableModel(new String[]{"ID Giao dịch", "Tên khách hàng", "Loại", "Số tiền", "Thời gian"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionPanel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> refreshTransactionList());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnRefresh);
        transactionPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void setupSystemPanel() {
        systemPanel = new JPanel(new BorderLayout(20, 20));
        systemPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel computerManagementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        computerManagementPanel.setBorder(BorderFactory.createTitledBorder("Thêm máy tính mới"));
        JTextField computerNameField = new JTextField(20);
        JButton addComputerButton = new JButton("Thêm máy");
        computerManagementPanel.add(new JLabel("Tên máy:"));
        computerManagementPanel.add(computerNameField);
        computerManagementPanel.add(addComputerButton);

        addComputerButton.addActionListener(e -> {
            String name = computerNameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên máy không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (computerDao.addComputer(name)) {
                JOptionPane.showMessageDialog(this, "Thêm máy thành công!");
                computerNameField.setText("");
                refreshComputerStatus();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm máy thất bại (có thể do trùng tên).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel menuManagementPanel = new JPanel(new BorderLayout(10, 10));
        menuManagementPanel.setBorder(BorderFactory.createTitledBorder("Quản lý Menu Dịch vụ"));

        menuTableModel = new DefaultTableModel(new String[]{"ID", "Tên món", "Giá", "Phân loại", "Trạng thái", "Kho"}, 0){
            public boolean isCellEditable(int row, int column) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        menuManagementPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel menuButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addMenuItemButton = new JButton("Thêm món mới");
        JButton editMenuItemButton = new JButton("Sửa món");
        JButton toggleMenuItemButton = new JButton("Ẩn / Hiện món");

        menuButtonPanel.add(addMenuItemButton);
        menuButtonPanel.add(editMenuItemButton);
        menuButtonPanel.add(toggleMenuItemButton);
        menuManagementPanel.add(menuButtonPanel, BorderLayout.SOUTH);

        addMenuItemButton.addActionListener(e -> showMenuItemDialog(null));
        editMenuItemButton.addActionListener(e -> {
            int selectedRow = menuTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = menuTable.convertRowIndexToModel(selectedRow);
                int itemId = (int) menuTableModel.getValueAt(modelRow, 0);
                MenuItem itemToEdit = menuItemDao.getAllItems().stream()
                        .filter(item -> item.getItemId() == itemId)
                        .findFirst().orElse(null);
                if (itemToEdit != null) {
                    showMenuItemDialog(itemToEdit);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        toggleMenuItemButton.addActionListener(e -> {
            int selectedRow = menuTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = menuTable.convertRowIndexToModel(selectedRow);
                int itemId = (int) menuTableModel.getValueAt(modelRow, 0);
                String currentState = (String) menuTableModel.getValueAt(modelRow, 4);
                boolean newAvailability = !currentState.equals("Đang bán");

                if(menuItemDao.toggleItemAvailability(itemId, newAvailability)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                    refreshMenuTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để thay đổi trạng thái.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        systemPanel.add(computerManagementPanel, BorderLayout.NORTH);
        systemPanel.add(menuManagementPanel, BorderLayout.CENTER);
    }

    private void showMenuItemDialog(MenuItem itemToEdit) {
        String title = (itemToEdit == null) ? "Thêm món mới" : "Sửa thông tin món";
        MenuItemDialog dialog = new MenuItemDialog(this, title, itemToEdit);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            MenuItem resultItem = dialog.getMenuItem();
            boolean success;
            if (itemToEdit == null) {
                success = menuItemDao.addMenuItem(resultItem);
            } else {
                success = menuItemDao.updateMenuItem(resultItem);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin món thành công!");
                refreshMenuTable();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thông tin thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshComputerStatus() {
        computerPanel.removeAll();
        List<Computer> computers = computerDao.getAllComputers();
        for (Computer computer : computers) {
            JPanel singleComputerPanel = new JPanel(new BorderLayout(5, 5));
            singleComputerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            JLabel nameLabel = new JLabel(computer.getComputerName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            JPanel infoPanel = new JPanel(new GridLayout(0, 1));
            infoPanel.setOpaque(false);
            JLabel statusLabel = new JLabel("Trạng thái: " + computer.getStatus(), SwingConstants.CENTER);
            infoPanel.add(statusLabel);
            singleComputerPanel.add(nameLabel, BorderLayout.NORTH);
            singleComputerPanel.add(infoPanel, BorderLayout.CENTER);
            if ("IN_USE".equals(computer.getStatus())) {
                Session activeSession = sessionDao.getActiveSessionByComputerId(computer.getComputerId());
                if (activeSession != null) {
                    JLabel userLabel = new JLabel("User: " + activeSession.getUsername(), SwingConstants.CENTER);
                    infoPanel.add(userLabel);
                }
            }
            updateComputerPanelColor(singleComputerPanel, computer.getStatus());
            addComputerPanelMouseListener(singleComputerPanel, computer);
            computerPanel.add(singleComputerPanel);
        }
        computerPanel.revalidate();
        computerPanel.repaint();
    }

    private void updateComputerPanelColor(JPanel panel, String status) {
        switch (status) {
            case "AVAILABLE": panel.setBackground(new Color(223, 240, 216)); break;
            case "IN_USE": panel.setBackground(new Color(252, 228, 214)); break;
            case "MAINTENANCE": panel.setBackground(new Color(232, 232, 232)); break;
        }
    }

    private void refreshCustomerList() {
        customerTableModel.setRowCount(0);
        List<Customer> customers = userDao.getAllCustomers();
        for (Customer customer : customers) {
            Vector<Object> row = new Vector<>();
            row.add(customer.getUserId());
            row.add(customer.getUsername());
            row.add(customer.getFullName());
            row.add(MONEY_FORMAT.format(customer.getBalance()));
            customerTableModel.addRow(row);
        }
    }

    private void refreshTransactionList() {
        transactionTableModel.setRowCount(0);
        List<Transaction> transactions = transactionDao.getAllTransactions();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        for (Transaction t : transactions) {
            Vector<Object> row = new Vector<>();
            row.add(t.getTransactionId());
            row.add(t.getUsername());
            row.add(t.getTransactionType());
            row.add(MONEY_FORMAT.format(t.getAmount()));
            row.add(t.getTransactionDate().format(formatter));
            transactionTableModel.addRow(row);
        }
    }

    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        List<MenuItem> items = menuItemDao.getAllItems();
        for (MenuItem item : items) {
            Vector<Object> row = new Vector<>();
            row.add(item.getItemId());
            row.add(item.getName());
            row.add(MONEY_FORMAT.format(item.getPrice()));
            row.add(item.getCategory());
            row.add(item.isAvailable() ? "Đang bán" : "Tạm ẩn");
            row.add(item.getStorage());
            menuTableModel.addRow(row);
        }
    }

    private void addComputerPanelMouseListener(JPanel panel, Computer computer) {
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    final String currentStatus = computer.getStatus();

                    if ("AVAILABLE".equals(currentStatus)) {
                        JMenuItem startItem = new JMenuItem("Mở máy cho khách...");
                        startItem.addActionListener(ae -> showStartSessionDialogWithSearch(computer));
                        popupMenu.add(startItem);
                    } else if ("IN_USE".equals(currentStatus)) {
                        JMenuItem endItem = new JMenuItem("Tắt máy & Tính tiền");

                        endItem.addActionListener(ae -> showEndSessionDialog(computer));

                        popupMenu.add(endItem);
                        popupMenu.addSeparator();
                        JMenuItem orderItem = new JMenuItem("Gọi đồ...");
                        orderItem.addActionListener(ae -> {
                            Session session = sessionDao.getActiveSessionByComputerId(computer.getComputerId());
                            if (session != null) {
                                Customer customer = userDao.getAllCustomers().stream().filter(c -> c.getUserId() == session.getUserId()).findFirst().orElse(null);
                                if (customer != null) {
                                    new OrderDialog(AdminDashboardFrame.this, session, customer).setVisible(true);
                                }
                            }
                        });
                        popupMenu.add(orderItem);
                    }
                    popupMenu.addSeparator();

                    // Sửa lỗi session
                    JMenuItem maintenanceItem = new JMenuItem();

                    if ("MAINTENANCE".equals(currentStatus)) {
                        maintenanceItem.setText("Chuyển sang chế độ Sẵn sàng");
                        maintenanceItem.addActionListener(ae -> {
                            computerDao.updateComputerStatus(computer.getComputerId(), "AVAILABLE");
                            refreshComputerStatus();
                        });
                    } else {
                        maintenanceItem.setText("Chuyển sang chế độ Bảo trì");
                        maintenanceItem.addActionListener(ae -> {
                            if ("IN_USE".equals(currentStatus)) {
                                int confirm = JOptionPane.showConfirmDialog(
                                        AdminDashboardFrame.this,
                                        "Máy này đang được sử dụng. Bạn có muốn tắt máy và thanh toán trước khi bảo trì không?",
                                        "Xác nhận Bảo trì",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE
                                );

                                if (confirm == JOptionPane.YES_OPTION) {
                                    boolean sessionEndedSuccessfully = showEndSessionDialog(computer);

                                    if (sessionEndedSuccessfully) {
                                        computerDao.updateComputerStatus(computer.getComputerId(), "MAINTENANCE");
                                        refreshComputerStatus();
                                    }
                                }

                            } else {
                                computerDao.updateComputerStatus(computer.getComputerId(), "MAINTENANCE");
                                refreshComputerStatus();
                            }
                        });
                    }
                    popupMenu.add(maintenanceItem);

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void showStartSessionDialogWithSearch(Computer computer) {
        List<Customer> allCustomers = userDao.getAllCustomers();
        if (allCustomers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có khách hàng nào trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        DefaultListModel<Customer> listModel = new DefaultListModel<>();
        allCustomers.forEach(listModel::addElement);
        JList<Customer> customerJList = new JList<>(listModel);
        customerJList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    Customer customer = (Customer) value;
                    setText(customer.getUsername() + " - " + customer.getFullName() + " (" + MONEY_FORMAT.format(customer.getBalance()) + ")");
                }
                return this;
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String filter = searchField.getText().toLowerCase();
                listModel.clear();
                for (Customer customer : allCustomers) {
                    if (customer.getUsername().toLowerCase().contains(filter) || customer.getFullName().toLowerCase().contains(filter)) {
                        listModel.addElement(customer);
                    }
                }
            }
        });
        panel.add(new JLabel("Tìm kiếm:"), BorderLayout.NORTH);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(new JScrollPane(customerJList), BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(350, 200));
        int result = JOptionPane.showConfirmDialog(this, panel, "Chọn khách hàng cho " + computer.getComputerName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Customer selectedCustomer = customerJList.getSelectedValue();
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Session newSession = sessionDao.startSession(selectedCustomer.getUserId(), computer.getComputerId());
            if (newSession != null) {
                computerDao.updateComputerStatus(computer.getComputerId(), "IN_USE");
                JOptionPane.showMessageDialog(this, "Đã mở " + computer.getComputerName() + " cho khách hàng " + selectedCustomer.getUsername());
                refreshComputerStatus();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể bắt đầu phiên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean showEndSessionDialog(Computer computer) {
        Session session = sessionDao.getActiveSessionByComputerId(computer.getComputerId());
        if (session == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiên hoạt động cho máy này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Customer customer = userDao.getAllCustomers().stream().filter(c -> c.getUserId() == session.getUserId()).findFirst().orElse(null);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Duration duration = Duration.between(session.getStartTime(), LocalDateTime.now());
        long secondsPlayed = duration.getSeconds();
        BigDecimal cost = HOURLY_RATE.multiply(new BigDecimal(secondsPlayed)).divide(new BigDecimal(3600), 2, BigDecimal.ROUND_HALF_UP);
        String message = String.format("<html>Khách hàng: <b>%s</b><br>Thời gian chơi: %s<br>Tổng tiền: <font color='red'>%s</font><br>Số dư hiện tại: %s</html>", session.getUsername(), formatDuration(duration), MONEY_FORMAT.format(cost), MONEY_FORMAT.format(customer.getBalance()));
        int result = JOptionPane.showConfirmDialog(this, message, "Xác nhận thanh toán cho " + computer.getComputerName(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            BigDecimal newBalance = customer.getBalance().subtract(cost);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Số dư không đủ. Vui lòng nạp thêm tiền.", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            boolean balanceUpdated = userDao.updateBalance(customer.getUserId(), newBalance);
            boolean sessionEnded = sessionDao.endSession(session.getSessionId(), cost);
            boolean transactionLogged = transactionDao.addTransaction(customer.getUserId(), cost, "PAYMENT");

            if (balanceUpdated && sessionEnded && transactionLogged) {
                computerDao.updateComputerStatus(computer.getComputerId(), "AVAILABLE");
                JOptionPane.showMessageDialog(this, "Thanh toán thành công. Số dư mới: " + MONEY_FORMAT.format(newBalance));
                refreshAll();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi xử lý thanh toán. Phiên CHƯA kết thúc. Vui lòng thử lại.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

    private void showAddCustomerDialog() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField fullNameField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên đăng nhập:")); panel.add(usernameField);
        panel.add(new JLabel("Mật khẩu:")); panel.add(passwordField);
        panel.add(new JLabel("Họ và Tên:")); panel.add(fullNameField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm khách hàng mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText(), password = new String(passwordField.getPassword()), fullName = fullNameField.getText();
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDao.addCustomer(username, password, fullName)) {
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
        JTextField amountField = new JTextField(15);
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            private final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            public void insertUpdate(DocumentEvent e) { formatText(); }
            public void removeUpdate(DocumentEvent e) { formatText(); }
            public void changedUpdate(DocumentEvent e) { formatText(); }
            private void formatText() {
                SwingUtilities.invokeLater(() -> {
                    try {
                        String text = amountField.getText().replaceAll("[^\\d]", "");
                        if (text.isEmpty()) return;
                        long value = Long.parseLong(text);
                        DocumentListener listener = this;
                        amountField.getDocument().removeDocumentListener(listener);
                        amountField.setText(formatter.format(value));
                        amountField.getDocument().addDocumentListener(listener);
                    } catch (NumberFormatException ex) {}
                });
            }
        });
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nhập số tiền cần nạp cho " + username + ":"));
        panel.add(amountField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Nạp tiền", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String amountStr = amountField.getText().replaceAll("[^\\d]", "");
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Customer customerToUpdate = userDao.getAllCustomers().stream().filter(c -> c.getUserId() == userId).findFirst().orElse(null);
            if (customerToUpdate == null) return;
            BigDecimal currentBalance = customerToUpdate.getBalance();
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền phải là số dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal newBalance = currentBalance.add(amount);
                if (userDao.updateBalance(userId, newBalance)) {
                    transactionDao.addTransaction(userId, amount, "TOP_UP");
                    JOptionPane.showMessageDialog(this, "Nạp tiền thành công!");
                    refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, "Nạp tiền thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }
}
