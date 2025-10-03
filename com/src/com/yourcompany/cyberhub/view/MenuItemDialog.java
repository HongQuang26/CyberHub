package com.yourcompany.cyberhub.view;

import com.yourcompany.cyberhub.model.MenuItem;
import javax.swing.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;

public class MenuItemDialog extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryComboBox;
    private boolean confirmed = false;
    private MenuItem menuItem;

    public MenuItemDialog(Frame parent, String title, MenuItem menuItem) {
        super(parent, title, true);
        this.menuItem = menuItem;

        setLayout(new GridLayout(4, 2, 10, 10));
        setSize(400, 200);
        setLocationRelativeTo(parent);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        nameField = new JTextField();
        priceField = new JTextField();
        categoryComboBox = new JComboBox<>(new String[]{"Đồ uống", "Đồ ăn vặt", "Món chính"});

        add(new JLabel("Tên món:"));
        add(nameField);
        add(new JLabel("Giá (VNĐ):"));
        add(priceField);
        add(new JLabel("Phân loại:"));
        add(categoryComboBox);

        JButton okButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        if (menuItem != null) {
            nameField.setText(menuItem.getName());
            priceField.setText(menuItem.getPrice().toPlainString());
            categoryComboBox.setSelectedItem(menuItem.getCategory());
        }

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());

        add(okButton);
        add(cancelButton);
    }

    private void onOK() {
        String name = nameField.getText();
        String priceStr = priceField.getText();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            String category = (String) categoryComboBox.getSelectedItem();

            if (menuItem == null) {
                menuItem = new MenuItem(0, name, price, category, true);
            } else {
                menuItem = new MenuItem(menuItem.getItemId(), name, price, category, menuItem.isAvailable());
            }

            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tiền phải là một con số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}