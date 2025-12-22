package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class RoleSelectionPage extends JFrame {

    public RoleSelectionPage() {
        setTitle("MultiMeal - Select Role");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Login As");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppConfig.PRIMARY_RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton customerRadio = new JRadioButton("Customer");
        JRadioButton adminRadio = new JRadioButton("Admin");
        
        customerRadio.setBackground(AppConfig.DARK_BACKGROUND);
        adminRadio.setBackground(AppConfig.DARK_BACKGROUND);
        customerRadio.setForeground(AppConfig.LIGHT_TEXT);
        adminRadio.setForeground(AppConfig.LIGHT_TEXT);
        customerRadio.setFont(AppConfig.FONT_BOLD);
        adminRadio.setFont(AppConfig.FONT_BOLD);
        
        ButtonGroup group = new ButtonGroup();
        group.add(customerRadio);
        group.add(adminRadio);
        
        customerRadio.setSelected(true);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        radioPanel.setBackground(AppConfig.DARK_BACKGROUND);
        radioPanel.add(customerRadio);
        radioPanel.add(adminRadio);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton proceedButton = new JButton("Proceed");
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));
        proceedButton.setMaximumSize(new Dimension(200, 40));
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        proceedButton.addActionListener(e -> {
            String role = customerRadio.isSelected() ? "customer" : "admin";
            new LoginPage(role).setVisible(true);
            dispose();
        });

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(proceedButton);

        add(mainPanel);
    }
}