package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HomeView extends JPanel {

    private final MainAppFrame parentFrame;
    private final int userId;

    public HomeView(MainAppFrame parentFrame, int userId) {
        this.parentFrame = parentFrame;
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        // Header now only provides spacing/branding instead of search
        add(createHeaderPanel(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppConfig.DARK_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(AppConfig.DARK_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Replaced searchField with a Title Label
        JLabel welcomeLabel = new JLabel("Explore Cuisines");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setForeground(AppConfig.PRIMARY_RED);

        headerPanel.add(welcomeLabel);
        return headerPanel;
    }

    private JPanel createMainContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppConfig.DARK_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Map<Integer, String> cuisines = loadCuisineData();

        for (Map.Entry<Integer, String> entry : cuisines.entrySet()) {
            contentPanel.add(createCategorySection(entry.getKey(), entry.getValue()));
            contentPanel.add(Box.createVerticalStrut(40));
        }

        return contentPanel;
    }

    private Map<Integer, String> loadCuisineData() {
        // Use LinkedHashMap to preserve insertion order from the SQL query
        Map<Integer, String> cuisines = new java.util.LinkedHashMap<>();
        // Order by cuisine_id to match the logical order in database_setup.sql (Desi
        // first, Pizza last)
        String query = "SELECT cuisine_id, name FROM Cuisines ORDER BY cuisine_id";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return cuisines;

            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                cuisines.put(rs.getInt("cuisine_id"), rs.getString("name"));
            }
        } catch (SQLException ex) {
            System.err.println("Error loading cuisines: " + ex.getMessage());
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
        return cuisines;
    }

    private JPanel createCategorySection(int cuisineId, String title) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setBackground(AppConfig.DARK_BACKGROUND);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel titleLabel = new JLabel(" " + title, JLabel.LEFT);
        titleLabel.setFont(AppConfig.FONT_LARGE);
        titleLabel.setForeground(AppConfig.LIGHT_TEXT);
        sectionPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel restaurantRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        restaurantRow.setBackground(AppConfig.DARK_BACKGROUND);

        loadRestaurantsByCuisine(restaurantRow, cuisineId, title);

        JScrollPane scrollPane = new JScrollPane(restaurantRow);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppConfig.DARK_BACKGROUND);

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    private void loadRestaurantsByCuisine(JPanel panel, int cuisineId, String cuisineName) {
        String query = "SELECT rest_id, name, image_path FROM Restaurants WHERE cuisine_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;

            pst = conn.prepareStatement(query);
            pst.setInt(1, cuisineId);
            rs = pst.executeQuery();

            while (rs.next()) {
                int restId = rs.getInt("rest_id");
                String name = rs.getString("name");
                String imagePath = rs.getString("image_path");

                panel.add(createRestaurantCard(restId, name, cuisineName, imagePath));
            }
        } catch (SQLException ex) {
            System.err.println("Error loading restaurants: " + ex.getMessage());
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private JPanel createRestaurantCard(int restId, String name, String cuisine, String imagePath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 220));
        card.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        card.setBackground(AppConfig.CARD_BACKGROUND);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel imgLabel = new JLabel("", JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(250, 140));
        imgLabel.setBackground(AppConfig.DARK_BACKGROUND);
        imgLabel.setOpaque(true);

        ImageIcon icon = AppConfig.loadImage(imagePath, 250, 140);

        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText("Image Not Found");
            imgLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
        }

        card.add(imgLabel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new BorderLayout(5, 0));
        textPanel.setBackground(AppConfig.CARD_BACKGROUND);
        textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(AppConfig.FONT_BOLD);
        nameLabel.setForeground(AppConfig.LIGHT_TEXT);

        JLabel cuisineLabel = new JLabel(cuisine);
        cuisineLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        cuisineLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);

        textPanel.add(nameLabel, BorderLayout.NORTH);
        textPanel.add(cuisineLabel, BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parentFrame.switchViewToMenu(restId, name);
            }
        });

        return card;
    }
}