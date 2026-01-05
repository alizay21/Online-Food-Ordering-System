package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class SignUpPage extends JFrame {

    private JTextField emailField, userField;
    private JPasswordField passField, confirmPassField;

    public SignUpPage() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650); // Height adjusted
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.DARK_BACKGROUND);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);

        JLabel titleLabel = new JLabel("MultiMeal Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = createTextField();
        userField = createTextField();
        passField = createPasswordField();
        confirmPassField = createPasswordField();

        JButton signUpButton = createThemedButton("REGISTER");
        JButton loginButton = createLinkButton("<html><u>Already have an account? Log In</u></html>");
        JButton backButton = createLinkButton("← Back to Role Selection");

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createLabeledField("Email:", emailField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createLabeledField("Username:", userField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createLabeledField("Password:", passField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createLabeledField("Confirm Pass:", confirmPassField));
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(signUpButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(backButton);

        signUpButton.addActionListener(e -> handleRegistration());
        loginButton.addActionListener(e -> {
            new LoginPage("customer").setVisible(true);
            dispose();
        });
        backButton.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true);
            dispose();
        });

        add(mainPanel, BorderLayout.CENTER);
        setTitle(titleLabel.getText());
    }

    private void handleRegistration() {
        String name = userField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (name.length() < 3 || name.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters and contain no spaces.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. Case-Sensitive Password Match
        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match exactly (check uppercase/lowercase).",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Users(username, password, email, role) VALUES(?, ?, ?, 'customer')";

        try (Connection connect = AppConfig.getConnection();
                PreparedStatement pst = connect.prepareStatement(query)) {

            if (connect == null)
                return;

            pst.setString(1, name);
            pst.setString(2, pass);
            pst.setString(3, email);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginPage("customer").setVisible(true);
            dispose();

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Username or Email already exists.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(AppConfig.FONT_INPUT);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(AppConfig.FONT_INPUT);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(Color.WHITE);
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