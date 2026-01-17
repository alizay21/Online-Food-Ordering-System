package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderHistoryPage extends JPanel {

    private final MainAppFrame parentFrame;
    private final int userId;
    private JPanel mainContentPanel;

    public OrderHistoryPage(MainAppFrame parentFrame, int userId) {
        this.parentFrame = parentFrame;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(AppConfig.DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(AppConfig.DARK_BACKGROUND);
        
        add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    public void refreshUI() {
        mainContentPanel.removeAll();
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainContentPanel.add(createRecommendedSection());
        mainContentPanel.add(Box.createVerticalStrut(40));
        mainContentPanel.add(createHistorySection());

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppConfig.CARD_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(" My Order History", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);

        JButton backBtn = new JButton("<- Back to Home");
        AppConfig.styleFlatButton(backBtn, AppConfig.BORDER_GRAY, AppConfig.LIGHT_TEXT);
        backBtn.addActionListener(e -> parentFrame.switchView(MainAppFrame.HOME_VIEW));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createRecommendedSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppConfig.CARD_BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(" Recommended Order (Most Frequently Ordered)");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setForeground(AppConfig.LIGHT_TEXT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));

        Map<String, Object> bestRecommendation = getBestRecommendedOrder();

        if (bestRecommendation.isEmpty()) {
            JLabel msg = new JLabel("Place a few orders to see your recommendations here.");
            msg.setForeground(AppConfig.PLACEHOLDER_TEXT);
            panel.add(msg);
        } else {
            panel.add(createRecommendationCard(
                (String) bestRecommendation.get("item_name"), 
                (String) bestRecommendation.get("rest_name"), 
                (double) bestRecommendation.get("price"), 
                (int) bestRecommendation.get("item_id"), 
                (int) bestRecommendation.get("rest_id")
            ));
        }
        return panel;
    }

    private Map<String, Object> getBestRecommendedOrder() {
        String query = "SELECT mi.item_id, mi.name AS item_name, mi.price, r.rest_id, r.name AS rest_name, " +
                       "COUNT(DISTINCT o.order_id) AS frequency " +
                       "FROM Orders o " +
                       "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                       "JOIN Menu_Items mi ON oi.item_id = mi.item_id " +
                       "JOIN Restaurants r ON mi.rest_id = r.rest_id " +
                       "WHERE o.user_id = ? AND o.status != 'Cancelled' " +
                       "GROUP BY mi.item_id, r.rest_id " +
                       "ORDER BY frequency DESC LIMIT 1";

        Map<String, Object> recommendation = new HashMap<>();
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    recommendation.put("item_id", rs.getInt("item_id"));
                    recommendation.put("rest_id", rs.getInt("rest_id"));
                    recommendation.put("item_name", rs.getString("item_name"));
                    recommendation.put("rest_name", rs.getString("rest_name"));
                    recommendation.put("price", rs.getDouble("price"));
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return recommendation;
    }

    private JPanel createRecommendationCard(String itemName, String restName, double price, int itemId, int restId) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setMaximumSize(new Dimension(800, 100));
        card.setBackground(AppConfig.DARK_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(AppConfig.PRIMARY_RED, 1));

        JLabel details = new JLabel("<html><div style='padding:10px;'><b>" + itemName + "</b><br>"
                + "<font color='#aaaaaa'>" + restName + "</font><br>"
                + "<b>Rs. " + String.format("%.2f", price) + "</b></div></html>");
        details.setForeground(AppConfig.LIGHT_TEXT);

        JButton orderBtn = new JButton("Quick Order");
        AppConfig.styleFlatButton(orderBtn, AppConfig.PRIMARY_RED, Color.WHITE);
        orderBtn.setPreferredSize(new Dimension(140, 40));
        orderBtn.addActionListener(e -> {
            if (Cart.addItem(itemId, price, restId, 1)) {
                parentFrame.switchView(MainAppFrame.CHECKOUT_VIEW);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 25));
        btnPanel.setBackground(AppConfig.DARK_BACKGROUND);
        btnPanel.add(orderBtn);

        card.add(details, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createHistorySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppConfig.DARK_BACKGROUND);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("History of Past Orders");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(AppConfig.LIGHT_TEXT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));

        loadOrderHistory(panel);
        return panel;
    }

    private void loadOrderHistory(JPanel panel) {
        // Updated query to include rest_id for feedback purposes
        String orderQuery = "SELECT o.order_id, o.rest_id, r.name AS rest_name, o.order_date, o.total_amount, o.status " +
                           "FROM Orders o JOIN Restaurants r ON o.rest_id = r.rest_id " +
                           "WHERE o.user_id = ? ORDER BY o.order_date DESC";

        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pstOrder = conn.prepareStatement(orderQuery)) {
            pstOrder.setInt(1, userId);
            try (ResultSet rsOrder = pstOrder.executeQuery()) {
                boolean hasOrders = false;
                while (rsOrder.next()) {
                    hasOrders = true;
                    panel.add(createOrderCard(rsOrder, conn));
                    panel.add(Box.createVerticalStrut(10));
                }
                if (!hasOrders) {
                    JLabel msg = new JLabel("You have no past orders.");
                    msg.setForeground(AppConfig.PLACEHOLDER_TEXT);
                    panel.add(msg);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private JPanel createOrderCard(ResultSet rs, Connection conn) throws SQLException {
        int orderId = rs.getInt("order_id");
        int restId = rs.getInt("rest_id");
        String restName = rs.getString("rest_name");
        String date = rs.getTimestamp("order_date").toString().substring(0, 16);
        double total = rs.getDouble("total_amount");
        String status = rs.getString("status");

        StringBuilder summary = new StringBuilder();
        String itemQuery = "SELECT mi.name, oi.quantity FROM Order_Items oi " +
                          "JOIN Menu_Items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?";
        try (PreparedStatement pstItem = conn.prepareStatement(itemQuery)) {
            pstItem.setInt(1, orderId);
            try (ResultSet rsItem = pstItem.executeQuery()) {
                while (rsItem.next()) {
                    summary.append(rsItem.getInt("quantity")).append("x ").append(rsItem.getString("name")).append(", ");
                }
            }
        }
        String itemText = summary.length() > 0 ? summary.substring(0, summary.length() - 2) : "No items";

        JPanel card = new JPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setBackground(AppConfig.CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Order #" + orderId + " | " + restName);
        header.setFont(AppConfig.FONT_BOLD);
        header.setForeground(AppConfig.LIGHT_TEXT);

        JLabel details = new JLabel("<html>" + itemText + "</html>");
        details.setForeground(AppConfig.PLACEHOLDER_TEXT);

        JLabel footer = new JLabel("Total: Rs. " + String.format("%.2f", total) + " | Status: " + status);
        footer.setForeground(AppConfig.LIGHT_TEXT);

        // --- BUTTONS LOGIC ---
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(AppConfig.CARD_BACKGROUND);

        // 1. Feedback Button (Only for Delivered orders)
        if ("Delivered".equalsIgnoreCase(status)) {
            JButton feedbackBtn = new JButton("Give Feedback");
            AppConfig.styleFlatButton(feedbackBtn, new Color(46, 204, 113), Color.WHITE); // Green
            
            if (checkFeedbackExists(orderId)) {
                feedbackBtn.setEnabled(false);
                feedbackBtn.setText("Feedback Sent");
                AppConfig.styleFlatButton(feedbackBtn, AppConfig.BORDER_GRAY, AppConfig.PLACEHOLDER_TEXT);
            } else {
                feedbackBtn.addActionListener(e -> showFeedbackDialog(orderId, restId, restName));
            }
            btns.add(feedbackBtn);
        }

        // 2. Reorder Button
        JButton reorderBtn = new JButton("Reorder All");
        AppConfig.styleFlatButton(reorderBtn, AppConfig.PRIMARY_RED, Color.WHITE);
        reorderBtn.setPreferredSize(new Dimension(130, 32));
        reorderBtn.addActionListener(e -> reorderOrder(orderId));
        btns.add(reorderBtn);

        // 3. Cancel Button (Only for Pending)
        if ("Pending".equalsIgnoreCase(status)) {
            JButton cancelBtn = new JButton("Cancel");
            AppConfig.styleFlatButton(cancelBtn, AppConfig.BORDER_GRAY, AppConfig.LIGHT_TEXT);
            cancelBtn.setPreferredSize(new Dimension(100, 32));
            cancelBtn.addActionListener(e -> cancelOrder(orderId));
            btns.add(cancelBtn);
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(AppConfig.CARD_BACKGROUND);
        bottom.add(footer, BorderLayout.WEST);
        bottom.add(btns, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void showFeedbackDialog(int orderId, int restId, String restName) {
        JDialog dialog = new JDialog(parentFrame, "Feedback for " + restName, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(AppConfig.DARK_BACKGROUND);
        
        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBackground(AppConfig.DARK_BACKGROUND);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Rate your experience (1-5):");
        title.setForeground(Color.WHITE);
        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        
        JLabel reviewLabel = new JLabel("Write a short review:");
        reviewLabel.setForeground(Color.WHITE);
        JTextArea commentArea = new JTextArea(4, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        content.add(title);
        content.add(ratingBox);
        content.add(reviewLabel);
        content.add(new JScrollPane(commentArea));

        JButton submit = new JButton("Submit Feedback");
        AppConfig.styleFlatButton(submit, AppConfig.PRIMARY_RED, Color.WHITE);
        submit.addActionListener(e -> {
            submitFeedback(orderId, restId, (int)ratingBox.getSelectedItem(), commentArea.getText());
            dialog.dispose();
            refreshUI();
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(submit, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void submitFeedback(int orderId, int restId, int rating, String comment) {
        String query = "INSERT INTO Feedbacks (order_id, user_id, rest_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, orderId);
            pst.setInt(2, userId);
            pst.setInt(3, restId);
            pst.setInt(4, rating);
            pst.setString(5, comment);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error submitting feedback: " + ex.getMessage());
        }
    }

    private boolean checkFeedbackExists(int orderId) {
        String query = "SELECT 1 FROM Feedbacks WHERE order_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) { return false; }
    }

    private void cancelOrder(int orderId) {
        int choice = JOptionPane.showConfirmDialog(this, "Cancel Order #" + orderId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = AppConfig.getConnection();
                 PreparedStatement pst = conn.prepareStatement("UPDATE Orders SET status = 'Cancelled' WHERE order_id = ?")) {
                pst.setInt(1, orderId);
                pst.executeUpdate();
                refreshUI();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void reorderOrder(int orderId) {
        String query = "SELECT oi.item_id, oi.quantity, mi.price, o.rest_id " +
                       "FROM Order_Items oi JOIN Orders o ON oi.order_id = o.order_id " +
                       "JOIN Menu_Items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            Cart.clear();
            while (rs.next()) {
                Cart.addItem(rs.getInt("item_id"), rs.getDouble("price"), rs.getInt("rest_id"), rs.getInt("quantity"));
            }
            parentFrame.switchView(MainAppFrame.CHECKOUT_VIEW);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}