package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class LoginPage extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JLabel titleLabel;
    private String selectedRole; 

    public LoginPage(String role) {
        this.selectedRole = role; 
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 520); // Height adjusted for extra button
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.DARK_BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);

        titleLabel = new JLabel("MultiMeal " + (selectedRole.equals("admin") ? "Admin" : "Customer") + " Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userField = createTextField();
        passField = createPasswordField();
        
        JButton loginButton = createThemedButton("LOGIN");
        JButton signUpButton = createLinkButton("<html><u>Don't have an account? Sign Up</u></html>");
        JButton backButton = createLinkButton("← Back to Role Selection");
        
        if (selectedRole.equals("admin")) {
            signUpButton.setVisible(false);
        }

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(createLabeledField("Username:", userField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createLabeledField("Password:", passField));
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(signUpButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(backButton);
        
        // Listeners
        loginButton.addActionListener(e -> handleLogin());
        signUpButton.addActionListener(e -> {
            new SignUpPage().setVisible(true);
            dispose();
        });
        backButton.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true); 
            dispose();
        });

        add(mainPanel, BorderLayout.CENTER);
        setTitle(titleLabel.getText());
    }

private void handleLogin() {
    String username = userField.getText().trim();
    String password = new String(passField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Login Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String query = "SELECT user_id FROM Users WHERE BINARY username = ? AND BINARY password = ? AND role = ?";

    try (Connection connect = AppConfig.getConnection();
         PreparedStatement pst = connect.prepareStatement(query)) {
        
        if (connect == null) return; 

        pst.setString(1, username);
        pst.setString(2, password);
        pst.setString(3, selectedRole);
        
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (selectedRole.equals("admin")) {
                new AdminPanel().setVisible(true);
            } else {
                new MainAppFrame(userId).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials (check case-sensitivity) or incorrect role.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(AppConfig.LIGHT_TEXT);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(AppConfig.LIGHT_TEXT);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return field;
    }

    private JButton createThemedButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }
    
    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(AppConfig.DARK_BACKGROUND);
        JLabel label = new JLabel(labelText);
        label.setFont(AppConfig.FONT_BOLD);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setPreferredSize(new Dimension(100, 20));
        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(field, BorderLayout.CENTER);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return rowPanel;
    }
}