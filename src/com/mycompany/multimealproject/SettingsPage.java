package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;

public class SettingsPage extends JPanel {

    private final int userId;
    private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField oldPassField, newPassField;

    public SettingsPage(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadUserProfile();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setBackground(AppConfig.CARD_BACKGROUND);

        JLabel label = new JLabel("My Settings & Profile");
        label.setFont(new Font("Arial", Font.BOLD, 26));
        label.setForeground(AppConfig.PRIMARY_RED);

        JButton close = new JButton("Close");
        AppConfig.styleFlatButton(close, AppConfig.BORDER_GRAY, AppConfig.LIGHT_TEXT);
        close.addActionListener(e -> {
            // In embedded mode, just hide by switching back to Home from MainAppFrame
            Container c = SwingUtilities.getAncestorOfClass(MainAppFrame.class, this);
            if (c instanceof MainAppFrame) {
                ((MainAppFrame) c).switchView(MainAppFrame.HOME_VIEW);
            }
        });

        panel.add(label, BorderLayout.WEST);
        panel.add(close, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 30, 40));
        content.setBackground(AppConfig.DARK_BACKGROUND);

        // --- 1. Profile Information Section ---
        JLabel profileHeading = createHeading("Profile Information");
        nameField = createField();
        emailField = createField();
        phoneField = createField();
        addressField = createField();

        JButton updateProfileBtn = createThemedButton("Update Profile");
        updateProfileBtn.addActionListener(e -> updateProfile());

        content.add(profileHeading);
        content.add(Box.createVerticalStrut(20));
        content.add(createLabeledRow("Full Name:", nameField));
        content.add(Box.createVerticalStrut(15));
        content.add(createLabeledRow("Email:", emailField));
        content.add(Box.createVerticalStrut(15));
        content.add(createLabeledRow("Phone:", phoneField));
        content.add(Box.createVerticalStrut(15));
        content.add(createLabeledRow("Address:", addressField));
        content.add(Box.createVerticalStrut(25));
        content.add(updateProfileBtn);

        content.add(Box.createVerticalStrut(40));

        // --- 2. Change Password Section ---
        JLabel passHeading = createHeading("Change Password");
        oldPassField = createPasswordField();
        newPassField = createPasswordField();

        JButton changePassBtn = createThemedButton("Change Password");
        changePassBtn.addActionListener(e -> changePassword());

        content.add(passHeading);
        content.add(Box.createVerticalStrut(20));
        content.add(createLabeledRow("Current Password:", oldPassField));
        content.add(Box.createVerticalStrut(15));
        content.add(createLabeledRow("New Password:", newPassField));
        content.add(Box.createVerticalStrut(25));
        content.add(changePassBtn);

        return new JScrollPane(content);
    }

    // --- Helper UI Methods (for SettingsPage) ---
    private JLabel createHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(AppConfig.PRIMARY_RED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createField() {
        JTextField field = new JTextField(30);
        field.setFont(AppConfig.FONT_INPUT);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        field.setMaximumSize(new Dimension(500, 35));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(30);
        field.setFont(AppConfig.FONT_INPUT);
        field.setBackground(AppConfig.CARD_BACKGROUND);
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        field.setMaximumSize(new Dimension(500, 35));
        return field;
    }

    private JButton createThemedButton(String text) {
        JButton button = new JButton(text);
        AppConfig.styleFlatButton(button, AppConfig.PRIMARY_RED, Color.WHITE);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JPanel createLabeledRow(String labelText, JComponent field) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setBackground(AppConfig.DARK_BACKGROUND);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel label = new JLabel(labelText);
        label.setFont(AppConfig.FONT_BOLD);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setPreferredSize(new Dimension(150, 30));

        rowPanel.add(label);
        rowPanel.add(field);
        return rowPanel;
    }

    // --- Database Logic (for SettingsPage) ---
    private void loadUserProfile() {
        String query = "SELECT username, email, phone, address FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;

            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                addressField.setText(rs.getString("address") != null ? rs.getString("address") : "");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void updateProfile() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        String query = "UPDATE Users SET username = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;

            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, address);
            pst.setInt(5, userId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Username or Email already exists.", "Update Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }

    private void changePassword() {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstSelect = null;
        PreparedStatement pstUpdate = null;
        ResultSet rs = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null)
                return;

            String selectQuery = "SELECT password FROM Users WHERE user_id = ? AND password = ?";
            pstSelect = conn.prepareStatement(selectQuery);
            pstSelect.setInt(1, userId);
            pstSelect.setString(2, oldPass);
            rs = pstSelect.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Current password incorrect.", "Authentication Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updateQuery = "UPDATE Users SET password = ? WHERE user_id = ?";
            pstUpdate = conn.prepareStatement(updateQuery);
            pstUpdate.setString(1, newPass);
            pstUpdate.setInt(2, userId);

            pstUpdate.executeUpdate();
            JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + ex.getMessage(), "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pstSelect, rs);
            if (pstUpdate != null) {
                try {
                    pstUpdate.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}