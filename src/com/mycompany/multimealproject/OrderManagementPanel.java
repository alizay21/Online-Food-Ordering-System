package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class OrderManagementPanel extends JPanel {

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JButton updateStatusButton;
    private int selectedOrderId = -1;

    public OrderManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppConfig.DARK_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createStatusUpdatePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadOrderData();
    }

    private JPanel createStatusUpdatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(AppConfig.CARD_BACKGROUND);
        
        JLabel label = createLabel("Update Status for Selected Order:");
        panel.add(label);

        String[] statuses = {"Pending", "Confirmed", "Delivered", "Cancelled"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setBackground(AppConfig.DARK_BACKGROUND);
        statusComboBox.setForeground(Color.BLACK);
        statusComboBox.setPreferredSize(new Dimension(150, 30));
        panel.add(statusComboBox);

        updateStatusButton = createActionButton("Update Status", AppConfig.PRIMARY_RED);
        updateStatusButton.setEnabled(false);
        updateStatusButton.addActionListener(e -> updateOrderStatus());
        panel.add(updateStatusButton);

        return panel;
    }
    
    private JPanel createTablePanel() {
        String[] columnNames = {"ID", "Customer", "Restaurant", "Date", "Total", "Address", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable = new JTable(tableModel);
        orderTable.setBackground(AppConfig.CARD_BACKGROUND);
        orderTable.setForeground(AppConfig.LIGHT_TEXT);
        orderTable.setSelectionBackground(AppConfig.PRIMARY_RED);
        orderTable.setSelectionForeground(Color.WHITE);
        orderTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));
        
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderTable.getSelectedRow() != -1) {
                int selectedRow = orderTable.getSelectedRow();
                selectedOrderId = (int) tableModel.getValueAt(selectedRow, 0); 
                String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();
                statusComboBox.setSelectedItem(currentStatus);
                updateStatusButton.setEnabled(true);
            } else {
                updateStatusButton.setEnabled(false);
                selectedOrderId = -1;
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        return label;
    }
 private JButton createActionButton(String text, Color bg) {
    JButton button = new JButton(text);
    // Even if you pass 'bg', the AppConfig method above will override it to PRIMARY_RED
    AppConfig.styleFlatButton(button, bg, Color.BLACK); 
    return button;
}

    private void loadOrderData() {
        String query = "SELECT o.order_id, u.username, r.name AS rest_name, o.order_date, o.total_amount, o.delivery_address, o.status " +
                       "FROM Orders o JOIN Users u ON o.user_id = u.user_id JOIN Restaurants r ON o.rest_id = r.rest_id ORDER BY o.order_id DESC";
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
                row.add(rs.getInt("order_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("rest_name"));
                row.add(rs.getTimestamp("order_date").toString());
                row.add(rs.getDouble("total_amount"));
                row.add(rs.getString("delivery_address"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }
    
    private void updateOrderStatus() {
        if (selectedOrderId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newStatus = (String) statusComboBox.getSelectedItem();

        String query = "UPDATE Orders SET status = ? WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            pst.setString(1, newStatus);
            pst.setInt(2, selectedOrderId);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order status updated successfully to " + newStatus + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOrderData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }
}