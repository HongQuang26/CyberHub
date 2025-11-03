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
    private JTextField storageField;
    private boolean confirmed = false;
    private MenuItem menuItem;

    public MenuItemDialog(Frame parent, String title, MenuItem menuItem) {
        super(parent, title, true);
        this.menuItem = menuItem;

        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(400, 200);
        setLocationRelativeTo(parent);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        nameField = new JTextField();
        priceField = new JTextField();
        categoryComboBox = new JComboBox<>(new String[]{"Đồ uống", "Đồ ăn vặt", "Món chính"});
        storageField = new JTextField("0");

        add(new JLabel("Tên món:"));
        add(nameField);
        add(new JLabel("Giá (VNĐ):"));
        add(priceField);
        add(new JLabel("Phân loại:"));
        add(categoryComboBox);
        add(new JLabel("Số lượng:")); // <-- ADD THIS
        add(storageField);

        JButton okButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        if (menuItem != null) {
            nameField.setText(menuItem.getName());
            priceField.setText(menuItem.getPrice().toPlainString());
            categoryComboBox.setSelectedItem(menuItem.getCategory());
            storageField.setText(String.valueOf(menuItem.getStorage()));
            //storageField.setEditable(false);
        }

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());

        add(okButton);
        add(cancelButton);
    }

    private void onOK() {
        String name = nameField.getText();
        String priceStr = priceField.getText();
        String storageStr = storageField.getText();

        if (name.isEmpty() || priceStr.isEmpty() || storageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            String category = (String) categoryComboBox.getSelectedItem();
            int storage = Integer.parseInt(storageStr);

            if (menuItem == null) {
                menuItem = new MenuItem(0, name, price, category, true, storage);
            } else {
                menuItem = new MenuItem(menuItem.getItemId(), name, price, category, menuItem.isAvailable(), storage);
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
