package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.dao.MenuItemDao;
import com.yourcompany.cyberhub.dao.OrderDao;
import com.yourcompany.cyberhub.dao.TransactionDao;
import com.yourcompany.cyberhub.dao.UserDao;
import com.yourcompany.cyberhub.model.*;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDialog extends JDialog {
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private JList<MenuItem> menuList;
    private DefaultListModel<MenuItem> menuListModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;

    private MenuItemDao menuItemDao;
    private OrderDao orderDao;
    private UserDao userDao;
    private TransactionDao transactionDao;

    private Session session;
    private Customer customer;
    private AdminDashboardFrame parentFrame;

    public OrderDialog(AdminDashboardFrame parent, Session session, Customer customer) {
        super(parent, "Gọi đồ cho: " + customer.getUsername(), true);
        this.session = session;
        this.customer = customer;
        this.parentFrame = parent;

        this.menuItemDao = new MenuItemDao();
        this.orderDao = new OrderDao();
        this.userDao = new UserDao();
        this.transactionDao = new TransactionDao();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuListModel = new DefaultListModel<>();
        menuList = new JList<>(menuListModel);
        menuList.setCellRenderer(new MenuItemRenderer());
        menuPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartTableModel = new DefaultTableModel(new String[]{"Tên món", "SL", "Đơn giá", "Thành tiền"}, 0){
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel middleButtonPanel = new JPanel();
        middleButtonPanel.setLayout(new BoxLayout(middleButtonPanel, BoxLayout.Y_AXIS));
        JButton addButton = new JButton(">>");
        JButton removeButton = new JButton("<<");
        middleButtonPanel.add(Box.createVerticalGlue());
        middleButtonPanel.add(addButton);
        middleButtonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        middleButtonPanel.add(removeButton);
        middleButtonPanel.add(Box.createVerticalGlue());

        JPanel confirmPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Tổng cộng: 0 đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton confirmButton = new JButton("Xác nhận Đơn hàng");
        confirmPanel.add(totalLabel, BorderLayout.WEST);
        confirmPanel.add(confirmButton, BorderLayout.EAST);
        cartPanel.add(confirmPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, cartPanel);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
        add(middleButtonPanel, BorderLayout.EAST);

        loadMenuItems();

        addButton.addActionListener(e -> addToCart());
        removeButton.addActionListener(e -> removeFromCart());
        confirmButton.addActionListener(e -> confirmOrder());
    }

    private void loadMenuItems() {
        menuListModel.clear();
        List<MenuItem> items = menuItemDao.getAllAvailableItems();
        for(MenuItem item : items) {
            menuListModel.addElement(item);
        }
    }

    private void addToCart() {
        MenuItem selectedItem = menuList.getSelectedValue();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong menu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this, "Nhập số lượng:", "1");
        if (quantityStr == null) return;

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if(quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentInCart = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            MenuItem itemInCart = (MenuItem) cartTableModel.getValueAt(i, 0);
            if (itemInCart.getItemId() == selectedItem.getItemId()) {
                currentInCart = (int) cartTableModel.getValueAt(i, 1);
                break;
            }
        }

        int totalDesired = currentInCart + quantity;
        if (totalDesired > selectedItem.getStorage()) {
            JOptionPane.showMessageDialog(this,
                    "Không đủ hàng trong kho. Chỉ còn " + selectedItem.getStorage() + " sản phẩm.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            MenuItem itemInCart = (MenuItem) cartTableModel.getValueAt(i, 0);
            if (itemInCart.getItemId() == selectedItem.getItemId()) {
                int oldQuantity = (int) cartTableModel.getValueAt(i, 1);
                cartTableModel.setValueAt(oldQuantity + quantity, i, 1);
                updateTotal();
                return;
            }
        }

        cartTableModel.addRow(new Object[]{selectedItem, quantity, MONEY_FORMAT.format(selectedItem.getPrice()), MONEY_FORMAT.format(selectedItem.getPrice().multiply(BigDecimal.valueOf(quantity)))});
        updateTotal();
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            cartTableModel.removeRow(selectedRow);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong giỏ hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            MenuItem item = (MenuItem) cartTableModel.getValueAt(i, 0);
            int quantity = (int) cartTableModel.getValueAt(i, 1);
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(quantity));
            cartTableModel.setValueAt(MONEY_FORMAT.format(lineTotal), i, 3);
            total = total.add(lineTotal);
        }
        totalLabel.setText("Tổng cộng: " + MONEY_FORMAT.format(total));
    }

    private void confirmOrder() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            MenuItem item = (MenuItem) cartTableModel.getValueAt(i, 0);
            int quantity = (int) cartTableModel.getValueAt(i, 1);
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
            details.add(new OrderDetail(item.getItemId(), quantity, item.getPrice()));
        }

        if (customer.getBalance().compareTo(totalAmount) < 0) {
            JOptionPane.showMessageDialog(this, "Số dư của khách không đủ để thanh toán. Vui lòng nạp thêm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đơn hàng trị giá " + MONEY_FORMAT.format(totalAmount) + "? Tiền sẽ bị trừ vào tài khoản khách.", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = new Order(customer.getUserId(), session.getSessionId(), totalAmount);
            boolean orderSuccess = orderDao.createOrder(order, details);
            if (orderSuccess) {
                BigDecimal newBalance = customer.getBalance().subtract(totalAmount);
                userDao.updateBalance(customer.getUserId(), newBalance);
                transactionDao.addTransaction(customer.getUserId(), totalAmount, "PAYMENT");
                JOptionPane.showMessageDialog(this, "Gọi đồ thành công!");
                parentFrame.refreshAll();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class MenuItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MenuItem) {
                MenuItem item = (MenuItem) value;
                setText(String.format("<html><b>%s</b><br><i>%s</i></html>", item.getName(), MONEY_FORMAT.format(item.getPrice())));
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }
            return this;
        }
    }
}
