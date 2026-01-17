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
    private JButton viewFeedbackButton; // New Button
    private int[] selectedRows = null;

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
        
        JLabel label = createLabel("Update Status for Selected Order(s):");
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

        // --- NEW VIEW FEEDBACK BUTTON ---
        viewFeedbackButton = createActionButton("View Feedback", new Color(46, 204, 113)); // Green color
        viewFeedbackButton.setEnabled(false);
        viewFeedbackButton.addActionListener(e -> showFeedbackDialog());
        panel.add(viewFeedbackButton);

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

        orderTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));
        
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRows = orderTable.getSelectedRows();
                if (selectedRows.length > 0) {
                    String currentStatus = tableModel.getValueAt(selectedRows[0], 6).toString();
                    statusComboBox.setSelectedItem(currentStatus);
                    
                    updateStatusButton.setEnabled(true);
                    updateStatusButton.setText("Update " + selectedRows.length + " Order(s)");

                    // Logic for View Feedback Button:
                    // Only enable if exactly ONE row is selected AND status is "Delivered"
                    if (selectedRows.length == 1 && "Delivered".equalsIgnoreCase(currentStatus)) {
                        viewFeedbackButton.setEnabled(true);
                    } else {
                        viewFeedbackButton.setEnabled(false);
                    }
                } else {
                    updateStatusButton.setEnabled(false);
                    updateStatusButton.setText("Update Status");
                    viewFeedbackButton.setEnabled(false);
                    selectedRows = null;
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void showFeedbackDialog() {
        if (selectedRows == null || selectedRows.length != 1) return;

        int orderId = (int) tableModel.getValueAt(selectedRows[0], 0);
        String customer = tableModel.getValueAt(selectedRows[0], 1).toString();
        String restaurant = tableModel.getValueAt(selectedRows[0], 2).toString();

        String query = "SELECT rating, comment, created_at FROM Feedbacks WHERE order_id = ?";

        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                String date = rs.getTimestamp("created_at").toString();

                // Create a popup dialog to show details
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order Feedback", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.getContentPane().setBackground(AppConfig.DARK_BACKGROUND);

                JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
                p.setBackground(AppConfig.DARK_BACKGROUND);
                p.setBorder(new EmptyBorder(20, 20, 20, 20));

                p.add(createFeedbackLabel("Order ID: " + orderId));
                p.add(createFeedbackLabel("Customer: " + customer));
                p.add(createFeedbackLabel("Restaurant: " + restaurant));
                p.add(createFeedbackLabel("Rating: " + rating + " / 5 Stars"));
                p.add(createFeedbackLabel("Date Submitted: " + date));
                
                JLabel reviewTitle = createFeedbackLabel("Review Content:");
                reviewTitle.setForeground(AppConfig.PRIMARY_RED);
                p.add(reviewTitle);

                JTextArea commentArea = new JTextArea(comment);
                commentArea.setEditable(false);
                commentArea.setLineWrap(true);
                commentArea.setWrapStyleWord(true);
                commentArea.setBackground(AppConfig.CARD_BACKGROUND);
                commentArea.setForeground(Color.WHITE);
                commentArea.setFont(new Font("Arial", Font.ITALIC, 14));
                
                JScrollPane scroll = new JScrollPane(commentArea);
                scroll.setPreferredSize(new Dimension(300, 100));
                scroll.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));

                dialog.add(p, BorderLayout.NORTH);
                dialog.add(scroll, BorderLayout.CENTER);

                JButton closeBtn = new JButton("Close");
                AppConfig.styleFlatButton(closeBtn, AppConfig.PRIMARY_RED, Color.WHITE);
                closeBtn.addActionListener(al -> dialog.dispose());
                dialog.add(closeBtn, BorderLayout.SOUTH);

                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "The customer has not provided feedback for this order yet.", "No Feedback", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching feedback: " + ex.getMessage());
        }
    }

    private JLabel createFeedbackLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        return l;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        return label;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text);
        AppConfig.styleFlatButton(button, bg, Color.WHITE); 
        return button;
    }

    private void loadOrderData() {
        String query = "SELECT o.order_id, u.username, r.name AS rest_name, o.order_date, o.total_amount, o.delivery_address, o.status " +
                       "FROM Orders o JOIN Users u ON o.user_id = u.user_id JOIN Restaurants r ON o.rest_id = r.rest_id ORDER BY o.order_id DESC";
        
        tableModel.setRowCount(0); 
        
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            
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
        }
    }
    
    private void updateOrderStatus() {
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one order to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newStatus = (String) statusComboBox.getSelectedItem();
        String query = "UPDATE Orders SET status = ? WHERE order_id = ?";
        
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;

            conn.setAutoCommit(false);
            pst = conn.prepareStatement(query);

            for (int rowIndex : selectedRows) {
                int orderId = (int) tableModel.getValueAt(rowIndex, 0);
                pst.setString(1, newStatus);
                pst.setInt(2, orderId);
                pst.addBatch();
            }

            pst.executeBatch();
            conn.commit();      

            JOptionPane.showMessageDialog(this, "Successfully updated " + selectedRows.length + " order(s) to " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOrderData();
            
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }
}