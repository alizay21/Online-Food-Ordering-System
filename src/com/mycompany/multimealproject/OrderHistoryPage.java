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
    
    public OrderHistoryPage(MainAppFrame parentFrame, int userId) {
        this.parentFrame = parentFrame;
        this.userId = userId;
        
        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContentScroll(), BorderLayout.CENTER);
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
    
    private JScrollPane createMainContentScroll() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 1. Recommended Order Section
        mainPanel.add(createRecommendedSection());
        mainPanel.add(Box.createVerticalStrut(40));
        
        // 2. Order History List Section
        mainPanel.add(createHistorySection());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppConfig.DARK_BACKGROUND);
        return scrollPane;
    }

    // --- Recommendation Logic & UI ---
    private JPanel createRecommendedSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppConfig.CARD_BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(" Recommended Order (Reorder your favorite!)");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setForeground(AppConfig.LIGHT_TEXT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));

        // Find the user's most frequently ordered item (Best Recommendation)
        Map<String, Object> bestRecommendation = getBestRecommendedOrder();
        
        if (bestRecommendation.isEmpty()) {
            JLabel msg = new JLabel("Place a few orders to see your recommendations here.");
            msg.setForeground(AppConfig.PLACEHOLDER_TEXT);
            panel.add(msg);
        } else {
            // Display the best recommended item/restaurant
            String itemName = (String) bestRecommendation.get("item_name");
            String restName = (String) bestRecommendation.get("rest_name");
            int itemId = (int) bestRecommendation.get("item_id");
            int restId = (int) bestRecommendation.get("rest_id");
            double price = (double) bestRecommendation.get("price");

            JPanel recoCard = createRecommendationCard(itemName, restName, price, itemId, restId);
            panel.add(recoCard);
        }

        return panel;
    }
    
    private JPanel createRecommendationCard(String itemName, String restName, double price, int itemId, int restId) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setMaximumSize(new Dimension(800, 100));
        card.setBackground(AppConfig.DARK_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));

        JLabel itemDetails = new JLabel(
            "<html><b>" + itemName + "</b> from <i>" + restName + "</i><br>Price: Rs. " + String.format("%.2f", price) + "</html>",
            SwingConstants.LEFT
        );
        itemDetails.setForeground(AppConfig.LIGHT_TEXT);
        itemDetails.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton reorderBtn = new JButton("Reorder 1 Item");
        AppConfig.styleFlatButton(reorderBtn, AppConfig.PRIMARY_RED, Color.WHITE);
        reorderBtn.setPreferredSize(new Dimension(150, 40));
        reorderBtn.addActionListener(e -> {
            // Logic: Add 1 recommended item to cart and switch to checkout
            boolean added = Cart.addItem(itemId, price, restId, 1);
            if (added) {
                parentFrame.switchView(MainAppFrame.CHECKOUT_VIEW);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(AppConfig.DARK_BACKGROUND);
        buttonPanel.add(reorderBtn);

        card.add(itemDetails, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);
        return card;
    }

    private Map<String, Object> getBestRecommendedOrder() {
        // Query finds the item_id and rest_id with the highest total quantity for this user
        String query = "SELECT " +
                       "    oi.item_id, oi.price_at_order, r.rest_id, r.name AS rest_name, mi.name AS item_name, SUM(oi.quantity) AS total_qty " +
                       "FROM Orders o " +
                       "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                       "JOIN Restaurants r ON o.rest_id = r.rest_id " +
                       "JOIN Menu_Items mi ON oi.item_id = mi.item_id " +
                       "WHERE o.user_id = ? " +
                       "GROUP BY oi.item_id, r.rest_id " +
                       "ORDER BY total_qty DESC " +
                       "LIMIT 1";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Map<String, Object> recommendation = new HashMap<>();

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return recommendation;
            
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            if (rs.next()) {
                recommendation.put("item_id", rs.getInt("item_id"));
                recommendation.put("rest_id", rs.getInt("rest_id"));
                recommendation.put("item_name", rs.getString("item_name"));
                recommendation.put("rest_name", rs.getString("rest_name"));
                recommendation.put("price", rs.getDouble("price_at_order"));
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching recommended order: " + ex.getMessage());
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
        return recommendation;
    }


    // --- History List Logic & UI ---
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
        // Query to get main order details
        String orderQuery = "SELECT o.order_id, r.name AS rest_name, o.order_date, o.total_amount, o.status " +
                            "FROM Orders o JOIN Restaurants r ON o.rest_id = r.rest_id " +
                            "WHERE o.user_id = ? ORDER BY o.order_date DESC";

        // Query to get items for a specific order (including size)
        String itemQuery = "SELECT mi.name, oi.quantity, oi.size_at_order FROM Order_Items oi " +
                           "JOIN Menu_Items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?";
        
        Connection conn = null;
        PreparedStatement pstOrder = null;
        PreparedStatement pstItem = null;
        ResultSet rsOrder = null;
        ResultSet rsItem = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            
            pstOrder = conn.prepareStatement(orderQuery);
            pstOrder.setInt(1, userId);
            rsOrder = pstOrder.executeQuery();

            boolean foundOrders = false;
            while (rsOrder.next()) {
                foundOrders = true;
                int orderId = rsOrder.getInt("order_id");
                String restName = rsOrder.getString("rest_name");
                String date = rsOrder.getTimestamp("order_date").toString().substring(0, 16);
                double total = rsOrder.getDouble("total_amount");
                String status = rsOrder.getString("status");

                // Get item details for this order
                StringBuilder itemSummary = new StringBuilder();
                pstItem = conn.prepareStatement(itemQuery);
                pstItem.setInt(1, orderId);
                rsItem = pstItem.executeQuery();
                
                while (rsItem.next()) {
                    itemSummary.append(rsItem.getInt("quantity"))
                               .append("x ")
                               .append(rsItem.getString("name"))
                               .append(" (")
                               .append(rsItem.getString("size_at_order"))
                               .append("), ");
                }
                
                // Remove trailing comma and space
                String summary = itemSummary.length() > 0 ? itemSummary.substring(0, itemSummary.length() - 2) : "No items listed";

                // Add the order card to the panel
                panel.add(createOrderCard(orderId, restName, date, total, status, summary));
                panel.add(Box.createVerticalStrut(10));
            }
            
            if (!foundOrders) {
                JLabel emptyMsg = new JLabel("You have no past orders.");
                emptyMsg.setForeground(AppConfig.PLACEHOLDER_TEXT);
                panel.add(emptyMsg);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading order history: " + ex.getMessage());
        } finally {
            AppConfig.closeResources(conn, pstOrder, rsOrder);
            AppConfig.closeResources(null, pstItem, rsItem); 
        }
    }

    private JPanel createOrderCard(int orderId, String restName, String date, double total, String status, String summary) {
        JPanel card = new JPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setBackground(AppConfig.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        Color statusColor = status.equals("Delivered") ? new Color(50, 200, 50) : AppConfig.PRIMARY_RED;

        JLabel header = new JLabel("Order #" + orderId + " | " + restName);
        header.setFont(AppConfig.FONT_BOLD);
        header.setForeground(AppConfig.LIGHT_TEXT);
        
        JLabel details = new JLabel(summary);
        details.setFont(new Font("Arial", Font.PLAIN, 12));
        details.setForeground(AppConfig.PLACEHOLDER_TEXT);
        
        JLabel footer = new JLabel("Total: Rs. " + String.format("%.2f", total) + 
                                  " | Date: " + date + 
                                  " | Status: <font color='#" + String.format("%06x", statusColor.getRGB() & 0x00FFFFFF) + "'><b>" + status + "</b></font>",
                                  SwingConstants.LEFT);
        footer.setForeground(AppConfig.LIGHT_TEXT);

        JButton reorderBtn = new JButton("Reorder This Order");
        AppConfig.styleFlatButton(reorderBtn, AppConfig.PRIMARY_RED, Color.WHITE);
        reorderBtn.setPreferredSize(new Dimension(160, 32));
        reorderBtn.addActionListener(e -> reorderOrder(orderId));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppConfig.CARD_BACKGROUND);
        bottomPanel.add(footer, BorderLayout.WEST);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setBackground(AppConfig.CARD_BACKGROUND);
        btnWrap.add(reorderBtn);
        bottomPanel.add(btnWrap, BorderLayout.EAST);

        card.setLayout(new BorderLayout());
        card.add(header, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    // Re-adds all items from a previous order back into the cart
    private void reorderOrder(int orderId) {
        String query = "SELECT oi.item_id, oi.quantity, oi.price_at_order, o.rest_id, o.packaging_type " +
                       "FROM Order_Items oi JOIN Orders o ON oi.order_id = o.order_id " +
                       "WHERE oi.order_id = ?";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;

            pst = conn.prepareStatement(query);
            pst.setInt(1, orderId);
            rs = pst.executeQuery();

            Cart.clear();
            boolean any = false;
            String packagingType = "Standard";

            while (rs.next()) {
                any = true;
                int itemId = rs.getInt("item_id");
                int qty = rs.getInt("quantity");
                double priceAtOrder = rs.getDouble("price_at_order");
                int restId = rs.getInt("rest_id");
                packagingType = rs.getString("packaging_type") != null ? rs.getString("packaging_type") : "Standard";

                Cart.setPackagingType(packagingType);
                Cart.addItem(itemId, priceAtOrder, restId, qty);
            }

            if (any) {
                parentFrame.switchView(MainAppFrame.CHECKOUT_VIEW);
            }
        } catch (SQLException ex) {
            System.err.println("Error while reordering order #" + orderId + ": " + ex.getMessage());
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }
}