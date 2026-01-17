package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class UserManagementPanel extends JPanel {

    private JTextField usernameField, emailField, phoneField, addressField;
    private JComboBox<String> roleComboBox;
    private JButton updateButton, deleteButton;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private int selectedUserId = -1;

    public UserManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppConfig.DARK_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createManagementForm(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadUserData();
    }

    private JPanel createManagementForm() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(AppConfig.CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppConfig.PRIMARY_RED), 
                "Manage User Details", 
                0, 0, AppConfig.FONT_BOLD, AppConfig.LIGHT_TEXT));

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBackground(AppConfig.CARD_BACKGROUND);

        fieldsPanel.add(createLabel("Username:"));
        usernameField = createTextField();
        fieldsPanel.add(usernameField);

        fieldsPanel.add(createLabel("Email:"));
        emailField = createTextField();
        fieldsPanel.add(emailField);
        
        fieldsPanel.add(createLabel("Phone:"));
        phoneField = createTextField();
        fieldsPanel.add(phoneField);
        
        fieldsPanel.add(createLabel("Address:"));
        addressField = createTextField();
        fieldsPanel.add(addressField);
        
        fieldsPanel.add(createLabel("Role:"));
        String[] roles = {"customer", "admin"};
        roleComboBox = createComboBox(roles);
        fieldsPanel.add(roleComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AppConfig.CARD_BACKGROUND);
        
        updateButton = createActionButton("Update Selected User", AppConfig.BORDER_GRAY);
        deleteButton = createActionButton("Delete Selected User", new Color(200, 50, 50));
        
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());

        return formPanel;
    }
    
    // --- Helper UI Methods ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        return label;
    }
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(AppConfig.DARK_BACKGROUND);
        field.setForeground(AppConfig.LIGHT_TEXT);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return field;
    }
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setBackground(AppConfig.DARK_BACKGROUND);
        box.setForeground(Color.BLACK);
        box.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return box;
    }
 private JButton createActionButton(String text, Color bg) {
    JButton button = new JButton(text);
    // Even if you pass 'bg', the AppConfig method above will override it to PRIMARY_RED
    AppConfig.styleFlatButton(button, bg, Color.BLACK); 
    return button;
}

    private JPanel createTablePanel() {
        String[] columnNames = {"ID", "Username", "Email", "Phone", "Address", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setBackground(AppConfig.CARD_BACKGROUND);
        userTable.setForeground(AppConfig.LIGHT_TEXT);
        userTable.setSelectionBackground(AppConfig.PRIMARY_RED);
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));
        
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                int selectedRow = userTable.getSelectedRow();
                selectedUserId = (int) tableModel.getValueAt(selectedRow, 0); 
                usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                phoneField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
                addressField.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
                roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                updateButton.setBackground(AppConfig.PRIMARY_RED);
            } else {
                 updateButton.setEnabled(false);
                 deleteButton.setEnabled(false);
                 updateButton.setBackground(AppConfig.BORDER_GRAY);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // --- Database Logic ---
    private void loadUserData() {
        String query = "SELECT user_id, username, email, phone, address, role FROM Users";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        tableModel.setRowCount(0); 
        
        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address"));
                row.add(rs.getString("role"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }
    
    private void updateUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText().trim();
        String address = addressField.getText();
        String role = (String) roleComboBox.getSelectedItem();
        
        if (!phone.isEmpty() && !phone.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, 
            "Phone number must contain only digits (0-9).", 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE);
        return; // This stops the method here and prevents the DB update
    }

        String query = "UPDATE Users SET username = ?, email = ?, phone = ?, address = ?, role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, address);
            pst.setString(5, role);
            pst.setInt(6, selectedUserId);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully.");
            loadUserData(); 
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }

    private void deleteUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete User ID: " + selectedUserId + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM Users WHERE user_id = ?";
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = AppConfig.getConnection();
                if (conn == null) return;
                pst = conn.prepareStatement(query);
                pst.setInt(1, selectedUserId);
                
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                loadUserData(); 
                clearFields();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user. Check for linked data (e.g., active orders).", "DB Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                AppConfig.closeResources(conn, pst, null);
            }
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        roleComboBox.setSelectedIndex(0);
        userTable.clearSelection();
        selectedUserId = -1;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setBackground(AppConfig.BORDER_GRAY);
    }
}