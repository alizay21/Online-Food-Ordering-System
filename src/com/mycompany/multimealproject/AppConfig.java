package com.mycompany.multimealproject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class AppConfig {
//(220, 220, 220)
    // --- DARK THEME COLOR PALETTE (Refined) ---
    public static final Color PRIMARY_RED = new Color(255, 90, 90); // Brighter Red for accents
    public static final Color DARK_BACKGROUND = new Color(24, 24, 24); // Deep Dark Gray/Black
    public static final Color CARD_BACKGROUND = new Color(36, 36, 36); // Card/Panel Background
    public static final Color BORDER_GRAY = new Color(60, 60, 60); // Subtle borders
    public static final Color LIGHT_TEXT = new Color(220, 220, 220); // Light Gray Text
    public static final Color PLACEHOLDER_TEXT = new Color(150, 150, 150); 
    public static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_LARGE = new Font("Arial", Font.BOLD, 24);

    // --- DATABASE CONNECTION CONFIGURATION ---
    private static final String URL = "jdbc:mysql://localhost:3306/multimeal_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    public static Connection getConnection() {
        Connection connect = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "JDBC Driver not found. Ensure the MySQL Connector JAR is added to Libraries.", "DB Setup Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Connection failed. Check XAMPP MySQL and database 'multimeal_db'.", "DB Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connect;
    }

    public static void closeResources(Connection conn, java.sql.Statement stmt, java.sql.ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    
    // --- UI HELPERS: Flat, solid buttons (no focus/rollover effects) ---
public static void styleFlatButton(JButton button, Color bg, Color fg) {
    if (button == null) return;
    
    // Force the colors you want
    button.setBackground(PRIMARY_RED); // Red background
    button.setForeground(new Color(30, 30, 30)); // Dark Gray/Black text
    
    button.setFont(FONT_BOLD);
    
    // STRIP ALL EFFECTS
    button.setFocusPainted(false);      // Removes the inner focus ring
    button.setBorderPainted(false);     // Removes the outer border/outline
    button.setContentAreaFilled(true);  // Ensures the red color fills the button
    button.setOpaque(true);             // Necessary for background color to show on some systems
    
    // Optional: add a tiny bit of padding so the text isn't cramped
    button.setMargin(new java.awt.Insets(5, 15, 5, 15));
}
    
    // --- IMAGE LOADING HELPER ---
    // Expects a relative path under the project root, e.g. "assets/images/pizza.jpg"
public static ImageIcon loadImage(String path, int width, int height) {
    if (path == null || path.trim().isEmpty()) return null;
    
    try {
        // This line is the secret to loading from Source Packages
        java.net.URL imgURL = AppConfig.class.getResource(path);
        
        if (imgURL == null) {
            System.err.println("Could not find file: " + path);
            return null;
        }

        ImageIcon icon = new ImageIcon(imgURL);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    } catch (Exception ex) {
        ex.printStackTrace();
        return null;
    }
}
}