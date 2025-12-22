package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

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
        backBtn.setForeground(AppConfig.LIGHT_TEXT);
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
        String query = "SELECT name, description, MIN(image_path) AS image_path " +
                       "FROM Menu_Items WHERE rest_id = ? AND stock > 0 " +
                       "GROUP BY name, description";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            
            pst = conn.prepareStatement(query);
            pst.setInt(1, restId);
            rs = pst.executeQuery();

            boolean foundAvailableItems = false;
            while (rs.next()) {
                foundAvailableItems = true;
                String name = rs.getString("name");
                String desc = rs.getString("description");
                String imagePath = rs.getString("image_path");
                
                // Card now also receives image path
                panel.add(createMenuCard(name, desc, imagePath));
                panel.add(Box.createVerticalStrut(15));
            }
            if (!foundAvailableItems) {
                 JLabel emptyLabel = new JLabel("No items are currently in stock for this restaurant.", JLabel.CENTER);
                 emptyLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
                 panel.add(emptyLabel);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading menu items: " + ex.getMessage());
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
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate it's clickable

        JLabel img = new JLabel("", JLabel.CENTER);
        img.setPreferredSize(new Dimension(120, 120));
        img.setOpaque(true);
        img.setBackground(AppConfig.DARK_BACKGROUND);
        img.setForeground(AppConfig.PLACEHOLDER_TEXT);
        ImageIcon icon = AppConfig.loadImage(imagePath, 120, 120);
        if (icon != null) {
            img.setIcon(icon);
        } else {
            img.setText("[Image]");
        }
        card.add(img, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.setBackground(AppConfig.CARD_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 10));

        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(AppConfig.FONT_LARGE);
        nameLabel.setForeground(AppConfig.LIGHT_TEXT);

        JLabel descLabel = new JLabel("<html><p style='width:300px;'>" + description + "</p></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
        
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
        ItemDetailsModal modal = new ItemDetailsModal(parentFrame, parentFrame, userId, restId, itemName, imagePath, description);
        modal.setVisible(true);
    }
    
}