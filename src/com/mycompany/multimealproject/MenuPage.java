package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;

public class MenuPage extends JPanel {

    private final MainAppFrame parentFrame;
    private final int userId;
    private final int restId;
    private final String restName;

    public MenuPage(MainAppFrame parentFrame, int userId, int restId, String restName) {
        this.parentFrame = parentFrame;
        this.userId = userId;
        this.restId = restId;
        this.restName = restName;

        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContentScroll(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppConfig.CARD_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Menu: " + restName, JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);

        JButton backBtn = new JButton("<- Back to Home");
        backBtn.setFont(AppConfig.FONT_BOLD);
        backBtn.setBackground(AppConfig.BORDER_GRAY);
        backBtn.setForeground(AppConfig.PRIMARY_RED);
        backBtn.addActionListener(e -> parentFrame.switchView(MainAppFrame.HOME_VIEW));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        return headerPanel;
    }

    private JScrollPane createMainContentScroll() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(AppConfig.DARK_BACKGROUND);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        loadMenuItems(menuPanel);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppConfig.DARK_BACKGROUND);
        return scrollPane;
    }

    private void loadMenuItems(JPanel panel) {
        // Query fetches all in-stock_amount items for this restaurant
        // We will deduplicate by name in Java to avoid SQL GROUP BY strictness issues
        String query = "SELECT name, description, image_path " +
                "FROM Menu_Items WHERE rest_id = ? AND stock_amount > 0 " +
                "ORDER BY name";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;

            pst = conn.prepareStatement(query);
            pst.setInt(1, restId);
            rs = pst.executeQuery();

            boolean foundAvailableItems = false;
            java.util.Set<String> processedItems = new java.util.HashSet<>();

            while (rs.next()) {
                String name = rs.getString("name");

                // If we've already displayed a card for this item name, skip (deduplication)
                if (processedItems.contains(name)) {
                    continue;
                }

                processedItems.add(name);
                foundAvailableItems = true;

                String desc = rs.getString("description");
                String imagePath = rs.getString("image_path");

                panel.add(createMenuCard(name, desc, imagePath));
                panel.add(Box.createVerticalStrut(15));
            }

            if (!foundAvailableItems) {
                JLabel emptyLabel = new JLabel("No items are currently in stock_amount for this restaurant.",
                        JLabel.CENTER);
                emptyLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
                emptyLabel.setFont(AppConfig.FONT_BOLD);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(emptyLabel);
            }

            // Force UI refresh
            panel.revalidate();
            panel.repaint();

        } catch (SQLException ex) {
            // Show error to user so they know something failed
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private JPanel createMenuCard(String itemName, String description, String imagePath) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        card.setBackground(AppConfig.CARD_BACKGROUND);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel img = new JLabel("", JLabel.CENTER);
        img.setPreferredSize(new Dimension(120, 120));
        img.setOpaque(true);
        img.setBackground(AppConfig.DARK_BACKGROUND);
        img.setForeground(AppConfig.PLACEHOLDER_TEXT);
        ImageIcon icon = AppConfig.loadImage(imagePath, 120, 120);
        if (icon != null)
            img.setIcon(icon);
        else
            img.setText("[Image]");

        card.add(img, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.setBackground(AppConfig.CARD_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 10));

        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(AppConfig.FONT_LARGE);
        nameLabel.setForeground(AppConfig.DARK_BACKGROUND);

        JLabel descLabel = new JLabel("<html><p style='width:300px;'>" + description + "</p></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(AppConfig.DARK_BACKGROUND);

        JLabel clickHint = new JLabel("Click for Sizes and Pricing ->", JLabel.RIGHT);
        clickHint.setFont(AppConfig.FONT_BOLD);
        clickHint.setForeground(AppConfig.PRIMARY_RED);

        centerPanel.add(nameLabel, BorderLayout.NORTH);
        centerPanel.add(descLabel, BorderLayout.CENTER);

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(clickHint, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openItemDetailsModal(itemName, imagePath, description);
            }
        });

        return card;
    }

    private void openItemDetailsModal(String itemName, String imagePath, String description) {
        ItemDetailsModal modal = new ItemDetailsModal(parentFrame, parentFrame, userId, restId, itemName, imagePath,
                description);
        modal.setVisible(true);
    }
}