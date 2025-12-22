package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class AdminPanel extends JFrame {

    private JPanel mainContentPanel; 

    public AdminPanel() {
        setTitle("MultiMeal Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainContentPanel.setBackground(AppConfig.DARK_BACKGROUND);
        
        JLabel welcomeLabel = new JLabel("Welcome, Admin! Select an option from the sidebar.", JLabel.CENTER);
        welcomeLabel.setForeground(AppConfig.LIGHT_TEXT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        mainContentPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        add(mainContentPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(AppConfig.PRIMARY_RED);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("ADMIN DASHBOARD", JLabel.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Log Out");
        
        // --- MODIFIED LOGOUT ACTION ---
        logoutBtn.addActionListener(e -> { 
            new RoleSelectionPage().setVisible(true); 
            dispose(); 
        });

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppConfig.CARD_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        String[] navItems = { "Manage Restaurants", "Manage Menu Items", "View Orders", "Manage Users"};

        for (String item : navItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 45));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
            
            button.addActionListener(e -> switchContent(item));
            
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(button);
        }
        return sidebar;
    }
    
    private void switchContent(String panelName) {
        mainContentPanel.removeAll();
        
        switch (panelName) {
            case "Manage Restaurants":
                mainContentPanel.add(new RestaurantManagementPanel(), BorderLayout.CENTER);
                break;
            case "Manage Menu Items":
                mainContentPanel.add(new MenuItemManagementPanel(), BorderLayout.CENTER);
                break;
            case "View Orders":
                mainContentPanel.add(new OrderManagementPanel(), BorderLayout.CENTER);
                break;
            case "Manage Users":
                mainContentPanel.add(new UserManagementPanel(), BorderLayout.CENTER);
                break;
            default:
                JLabel content = new JLabel("Welcome, Admin! Overview data goes here.", JLabel.CENTER);
                content.setForeground(AppConfig.LIGHT_TEXT);
                content.setFont(new Font("Arial", Font.PLAIN, 20));
                mainContentPanel.add(content, BorderLayout.CENTER);
                break;
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}