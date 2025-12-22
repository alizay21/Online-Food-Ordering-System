package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SplashPage extends JFrame {

    public SplashPage() {
        setTitle("MultiMeal - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(AppConfig.DARK_BACKGROUND);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);

        JLabel titleLabel = new JLabel(" MultiMeal", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(AppConfig.PRIMARY_RED);
        buttonPanel.setPreferredSize(new Dimension(250, 60));
        
        JLabel buttonText = new JLabel("Let's Eat!!");
        buttonText.setForeground(Color.WHITE);
        buttonText.setFont(new Font("Arial", Font.BOLD, 28));
        buttonPanel.add(buttonText);
        
        // --- Click Action ---
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RoleSelectionPage().setVisible(true);
                dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) { buttonPanel.setBackground(AppConfig.PRIMARY_RED.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { buttonPanel.setBackground(AppConfig.PRIMARY_RED); }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 50, 0);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }
}