package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class MenuItemManagementPanel extends JPanel {

    private JComboBox<String> restaurantComboBox;
    private JComboBox<String> sizeComboBox;

    private JTextField nameField, priceField, stock_amountField, imagePathField, packagingFeeField;
    private JTextArea descArea;
    private JButton addButton, updateButton, deleteButton;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private int selectedItemId = -1;
    private String selectedItemSize = null;
    private int currentRestId = -1;

    public MenuItemManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppConfig.DARK_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createManagementForm(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadRestaurantList();
    }

    private JPanel createManagementForm() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(AppConfig.CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppConfig.PRIMARY_RED),
                "Manage Menu Items",
                0, 0, AppConfig.FONT_BOLD, AppConfig.LIGHT_TEXT));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(AppConfig.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(createLabel("Restaurant:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        restaurantComboBox = createComboBox();
        fieldsPanel.add(restaurantComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        fieldsPanel.add(createLabel("Item Name:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        nameField = createTextField();
        fieldsPanel.add(nameField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(createLabel("Size:"), gbc);
        String[] sizes = { "Small", "Medium", "Large", "Standard" };
        gbc.gridx = 1;
        gbc.gridy = 1;
        sizeComboBox = createComboBox(sizes);
        fieldsPanel.add(sizeComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        fieldsPanel.add(createLabel("Price (Rs.):"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        priceField = createTextField();
        fieldsPanel.add(priceField, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        fieldsPanel.add(createLabel("Stock Amount:"), gbc);
        gbc.gridx = 5;
        gbc.gridy = 1;
        stock_amountField = createTextField();
        fieldsPanel.add(stock_amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(createLabel("Image Path:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        imagePathField = createTextField();
        fieldsPanel.add(imagePathField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3;
        gbc.gridy = 2;
        fieldsPanel.add(createLabel("Packaging Fee (Rs.):"), gbc);
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        packagingFeeField = createTextField();
        fieldsPanel.add(packagingFeeField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        fieldsPanel.add(createLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        descArea = createTextArea();
        JScrollPane scrollDesc = new JScrollPane(descArea);
        scrollDesc.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(scrollDesc, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AppConfig.CARD_BACKGROUND);

        // Updated button creation to match UserManagement style
        addButton = createActionButton("Add Item/Size", AppConfig.PRIMARY_RED);
        updateButton = createActionButton("Update Item/Size", AppConfig.BORDER_GRAY);
        deleteButton = createActionButton("Delete Item/Size", new Color(200, 50, 50));

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        restaurantComboBox.addActionListener(e -> loadMenuItems());
        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());

        return formPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setBackground(AppConfig.DARK_BACKGROUND);
        field.setForeground(AppConfig.LIGHT_TEXT);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(3, 15);
        area.setBackground(AppConfig.DARK_BACKGROUND);
        area.setForeground(AppConfig.LIGHT_TEXT);
        area.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretColor(Color.WHITE);
        return area;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setBackground(AppConfig.DARK_BACKGROUND);
        box.setForeground(Color.BLACK);
        box.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return box;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> box = new JComboBox<>();
        box.setBackground(AppConfig.DARK_BACKGROUND);
        box.setForeground(Color.BLACK);
        box.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return box;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text);
        // Using the same styling method as UserManagementPanel
        AppConfig.styleFlatButton(button, bg, Color.WHITE);
        return button;
    }

    private JPanel createTablePanel() {
        String[] columnNames = { "ID", "Name", "Size", "Price", "Stock", "Description", "Image Path", "Pkg. Fee" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setBackground(AppConfig.CARD_BACKGROUND);
        menuTable.setForeground(AppConfig.LIGHT_TEXT);
        menuTable.setSelectionBackground(AppConfig.PRIMARY_RED);
        menuTable.setSelectionForeground(Color.WHITE);
        menuTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));

        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                int selectedRow = menuTable.getSelectedRow();

                selectedItemId = (int) tableModel.getValueAt(selectedRow, 0);
                selectedItemSize = tableModel.getValueAt(selectedRow, 2).toString();

                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                sizeComboBox.setSelectedItem(selectedItemSize);
                priceField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                stock_amountField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                descArea.setText(tableModel.getValueAt(selectedRow, 5).toString());
                imagePathField.setText(tableModel.getValueAt(selectedRow, 6).toString());

                Object feeValue = tableModel.getValueAt(selectedRow, 7);
                packagingFeeField.setText(feeValue != null ? feeValue.toString() : "0.0");

                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                // Maintain flat style when changing color
                AppConfig.styleFlatButton(updateButton, AppConfig.PRIMARY_RED, Color.WHITE);
            } else {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
                // Maintain flat style when changing color
                AppConfig.styleFlatButton(updateButton, AppConfig.BORDER_GRAY, Color.WHITE);
                selectedItemSize = null;
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadRestaurantList() {
        String query = "SELECT rest_id, name FROM Restaurants";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            restaurantComboBox.removeAllItems();
            while (rs.next()) {
                restaurantComboBox.addItem(rs.getInt("rest_id") + " - " + rs.getString("name"));
            }
            if (restaurantComboBox.getItemCount() > 0) {
                restaurantComboBox.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading restaurants: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void loadMenuItems() {
        if (restaurantComboBox.getSelectedItem() == null) {
            tableModel.setRowCount(0);
            return;
        }
        String selected = restaurantComboBox.getSelectedItem().toString();
        currentRestId = Integer.parseInt(selected.split(" - ")[0]);

        String query = "SELECT item_id, name, size, price, description, stock_amount, image_path, packaging_fee FROM Menu_Items WHERE rest_id = ? ORDER BY item_id, size";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        tableModel.setRowCount(0);

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;
            pst = conn.prepareStatement(query);
            pst.setInt(1, currentRestId);
            rs = pst.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("item_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("size"));
                row.add(rs.getDouble("price"));
                row.add(rs.getInt("stock_amount"));
                row.add(rs.getString("description"));
                row.add(rs.getString("image_path"));
                row.add(rs.getDouble("packaging_fee"));
                tableModel.addRow(row);
            }
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void addItem() {
        if (currentRestId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = nameField.getText();
        String size = (String) sizeComboBox.getSelectedItem();
        String desc = descArea.getText();
        String priceStr = priceField.getText();
        String stock_amountStr = stock_amountField.getText();
        String imagePath = imagePathField.getText();
        String packagingFeeStr = packagingFeeField.getText();
        double price;
        int stock_amount;
        double packagingFee = 0.0;

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            stock_amount = Integer.parseInt(stock_amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid stock_amount format. Must be an integer.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!packagingFeeStr.isEmpty()) {
            try {
                packagingFee = Double.parseDouble(packagingFeeStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid packaging fee format.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int newItemId = getNextItemId(currentRestId, name);
        if (newItemId == -1)
            return;

        String query = "INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;
            pst = conn.prepareStatement(query);
            pst.setInt(1, newItemId);
            pst.setInt(2, currentRestId);
            pst.setString(3, name);
            pst.setString(4, size);
            pst.setString(5, desc);
            pst.setDouble(6, price);
            pst.setInt(7, stock_amount);
            pst.setString(8, imagePath);
            pst.setDouble(9, packagingFee);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Menu item size variant added successfully.");
            loadMenuItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding menu item: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }

    private void updateItem() {
        if (selectedItemId == -1 || selectedItemSize == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = nameField.getText();
        String newSize = (String) sizeComboBox.getSelectedItem();
        String desc = descArea.getText();
        String priceStr = priceField.getText();
        String stock_amountStr = stock_amountField.getText();
        String imagePath = imagePathField.getText();
        String packagingFeeStr = packagingFeeField.getText();
        double price;
        int stock_amount;
        double packagingFee = 0.0;

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            return;
        }
        try {
            stock_amount = Integer.parseInt(stock_amountStr);
        } catch (NumberFormatException e) {
            return;
        }
        if (!packagingFeeStr.isEmpty()) {
            try {
                packagingFee = Double.parseDouble(packagingFeeStr);
            } catch (NumberFormatException e) {
            }
        }

        String query = "UPDATE Menu_Items SET name = ?, size = ?, description = ?, price = ?, stock_amount = ?, image_path = ?, packaging_fee = ? WHERE item_id = ? AND size = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, newSize);
            pst.setString(3, desc);
            pst.setDouble(4, price);
            pst.setInt(5, stock_amount);
            pst.setString(6, imagePath);
            pst.setDouble(7, packagingFee);
            pst.setInt(8, selectedItemId);
            pst.setString(9, selectedItemSize);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Menu item variant updated successfully.");
            loadMenuItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating menu item: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }

    private void deleteItem() {
        if (selectedItemId == -1 || selectedItemSize == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected variant?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM Menu_Items WHERE item_id = ? AND size = ?";
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = AppConfig.getConnection();
                pst = conn.prepareStatement(query);
                pst.setInt(1, selectedItemId);
                pst.setString(2, selectedItemSize);
                pst.executeUpdate();
                loadMenuItems();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage());
            } finally {
                AppConfig.closeResources(conn, pst, null);
            }
        }
    }

    private int getNextItemId(int restId, String name) {
        String existingIdQuery = "SELECT item_id FROM Menu_Items WHERE rest_id = ? AND name = ? LIMIT 1";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = AppConfig.getConnection();
            pst = conn.prepareStatement(existingIdQuery);
            pst.setInt(1, restId);
            pst.setString(2, name);
            rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt("item_id");

            String nextIdQuery = "SELECT MAX(item_id) FROM Menu_Items";
            pst = conn.prepareStatement(nextIdQuery);
            rs = pst.executeQuery();
            return rs.next() ? rs.getInt(1) + 1 : 1;
        } catch (SQLException ex) {
            return -1;
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        stock_amountField.setText("");
        descArea.setText("");
        imagePathField.setText("");
        packagingFeeField.setText("");
        sizeComboBox.setSelectedIndex(0);
        menuTable.clearSelection();
        selectedItemId = -1;
        selectedItemSize = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        // Reset update button style to flat gray
        AppConfig.styleFlatButton(updateButton, AppConfig.BORDER_GRAY, Color.WHITE);
        nameField.requestFocusInWindow();
    }
}