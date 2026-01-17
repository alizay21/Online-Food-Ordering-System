package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
//import javax.swing.border.EmptyBorder;

public class MainAppFrame extends JFrame {

    private final int userId;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    public static final String HOME_VIEW = "HomeView";
    public static final String MENU_VIEW = "MenuView";
    public static final String CHECKOUT_VIEW = "CheckoutView";
   
    public static final String HISTORY_VIEW = "HistoryView"; 
    public static final String SETTINGS_VIEW = "SettingsView";

    public MainAppFrame(int userId) {
        this.userId = userId;
        
        setTitle("MultiMeal Food Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppConfig.DARK_BACKGROUND);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        initViews();
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppConfig.CARD_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("MultiMeal", JLabel.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(AppConfig.PRIMARY_RED);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createNavButton(" Home", HOME_VIEW));
        sidebar.add(createNavButton("My Cart", CHECKOUT_VIEW));
        
        sidebar.add(createNavButton(" My Orders", HISTORY_VIEW)); 
        sidebar.add(createNavButton("️ Settings", SETTINGS_VIEW));
        sidebar.add(Box.createVerticalGlue());
        
        sidebar.add(createNavButton("Log Out", "Logout"));
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String command) {
        JButton button = new JButton(text);
        button.setFont(AppConfig.FONT_BOLD);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        
        button.addActionListener(e -> handleNavigation(command));
        return button;
    }
    
    private void initViews() {
        HomeView homeView = new HomeView(this, userId);
        contentPanel.add(homeView, HOME_VIEW);
        
        contentPanel.add(new JPanel(), MENU_VIEW); 
        contentPanel.add(new JPanel(), CHECKOUT_VIEW);
        contentPanel.add(new JPanel(), HISTORY_VIEW); 
        SettingsPage settingsView = new SettingsPage(userId);
        contentPanel.add(settingsView, SETTINGS_VIEW);
        
        cardLayout.show(contentPanel, HOME_VIEW);
    }

    public void switchView(String viewName) {
        cardLayout.show(contentPanel, viewName);
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    public void switchViewToMenu(int restId, String restName) {
        MenuPage menuView = new MenuPage(this, userId, restId, restName);
        contentPanel.add(menuView, MENU_VIEW);
        cardLayout.show(contentPanel, MENU_VIEW);
    }
    
    private void handleNavigation(String command) {
        if (command.equals(HOME_VIEW)) {
            switchView(HOME_VIEW);
        } else if (command.equals(CHECKOUT_VIEW)) {
            CheckoutPage checkoutView = new CheckoutPage(this, userId);
            contentPanel.add(checkoutView, CHECKOUT_VIEW);
            switchView(CHECKOUT_VIEW);
        } else if (command.equals(HISTORY_VIEW)) { 
            OrderHistoryPage historyView = new OrderHistoryPage(this, userId);
            contentPanel.add(historyView, HISTORY_VIEW);
            switchView(HISTORY_VIEW);
        } else if (command.equals(SETTINGS_VIEW)) {
            switchView(SETTINGS_VIEW);
        } else if (command.equals("Logout")) {
            Cart.clear();
            new RoleSelectionPage().setVisible(true);
            dispose();
        }
    }
}