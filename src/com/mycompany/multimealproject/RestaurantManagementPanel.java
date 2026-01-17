package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class RestaurantManagementPanel extends JPanel {

    private JTextField nameField, imagePathField;
    private JComboBox<String> cuisineComboBox;
    private JButton addButton, updateButton, deleteButton;
    private JTable restaurantTable;
    private DefaultTableModel tableModel;
    private JLabel imagePreviewLabel; // NEW: Label to display the actual picture
    private int selectedRestId = -1; 

    public RestaurantManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppConfig.DARK_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createManagementForm(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadCuisineData();
        loadRestaurantData();
    }

    private JPanel createManagementForm() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(AppConfig.CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppConfig.PRIMARY_RED), 
                "Manage Restaurant Details", 
                0, 0, AppConfig.FONT_BOLD, AppConfig.LIGHT_TEXT));

        // Container for fields and the preview
        JPanel contentPanel = new JPanel(new BorderLayout(20, 10));
        contentPanel.setBackground(AppConfig.CARD_BACKGROUND);

        // Fields Panel (Left Side)
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        fieldsPanel.setBackground(AppConfig.CARD_BACKGROUND);

        fieldsPanel.add(createLabel("Restaurant Name:"));
        nameField = createTextField();
        fieldsPanel.add(nameField);

        fieldsPanel.add(createLabel("Cuisine Type:"));
        cuisineComboBox = createComboBox();
        fieldsPanel.add(cuisineComboBox);
        
        fieldsPanel.add(createLabel("Image Path:"));
        imagePathField = createTextField();
        // Update preview as user types or pastes
        imagePathField.addActionListener(e -> updateImagePreview()); 
        fieldsPanel.add(imagePathField);

        // Preview Panel (Right Side)
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(AppConfig.CARD_BACKGROUND);
        previewPanel.setPreferredSize(new Dimension(150, 120));
        previewPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppConfig.BORDER_GRAY), "Preview", 
                0, 0, AppConfig.FONT_BOLD, AppConfig.PLACEHOLDER_TEXT));

        imagePreviewLabel = new JLabel("No Image", SwingConstants.CENTER);
        imagePreviewLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
        previewPanel.add(imagePreviewLabel, BorderLayout.CENTER);

        contentPanel.add(fieldsPanel, BorderLayout.CENTER);
        contentPanel.add(previewPanel, BorderLayout.EAST);
        
        // Buttons Panel (Bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AppConfig.CARD_BACKGROUND);
        
        addButton = createActionButton("Add Restaurant", AppConfig.PRIMARY_RED);
        updateButton = createActionButton("Update Restaurant", AppConfig.BORDER_GRAY);
        deleteButton = createActionButton("Delete Restaurant", new Color(200, 50, 50));
        
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        formPanel.add(contentPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addRestaurant());
        updateButton.addActionListener(e -> updateRestaurant());
        deleteButton.addActionListener(e -> deleteRestaurant());

        return formPanel;
    }

    // NEW: Method to load and display the image
    private void updateImagePreview() {
        String path = imagePathField.getText().trim();
        if (!path.isEmpty()) {
            // Uses the helper you already have in AppConfig
            ImageIcon icon = AppConfig.loadImage(path, 130, 100); 
            if (icon != null) {
                imagePreviewLabel.setIcon(icon);
                imagePreviewLabel.setText("");
            } else {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("Invalid Path");
            }
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image");
        }
    }

    private JPanel createTablePanel() {
        String[] columnNames = {"ID", "Name", "Cuisine", "Image Path"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        restaurantTable = new JTable(tableModel);
        restaurantTable.setBackground(AppConfig.CARD_BACKGROUND);
        restaurantTable.setForeground(AppConfig.LIGHT_TEXT);
        restaurantTable.setSelectionBackground(AppConfig.PRIMARY_RED);
        restaurantTable.setSelectionForeground(Color.WHITE);
        restaurantTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(restaurantTable);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY));
        
        restaurantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && restaurantTable.getSelectedRow() != -1) {
                int selectedRow = restaurantTable.getSelectedRow();
                selectedRestId = (int) tableModel.getValueAt(selectedRow, 0); 
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                cuisineComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                
                String path = tableModel.getValueAt(selectedRow, 3).toString();
                imagePathField.setText(path);
                updateImagePreview(); // Show the image when row is clicked
                
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                updateButton.setBackground(AppConfig.PRIMARY_RED);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- Helper UI Methods ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(AppConfig.DARK_BACKGROUND);
        field.setForeground(AppConfig.LIGHT_TEXT);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return field;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> box = new JComboBox<>();
        box.setBackground(AppConfig.DARK_BACKGROUND);
        box.setForeground(Color.BLACK);
        box.setBorder(BorderFactory.createLineBorder(AppConfig.BORDER_GRAY, 1));
        return box;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text);
        AppConfig.styleFlatButton(button, bg, Color.BLACK);
        return button;
    }

    private void clearFields() {
        nameField.setText("");
        imagePathField.setText("");
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("No Image");
        if (cuisineComboBox.getItemCount() > 0) cuisineComboBox.setSelectedIndex(0);
        restaurantTable.clearSelection();
        selectedRestId = -1;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setBackground(AppConfig.BORDER_GRAY);
        nameField.requestFocusInWindow();
    }

    // --- Database Logic ---
    private void loadCuisineData() {
        String query = "SELECT name FROM Cuisines";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            
            cuisineComboBox.removeAllItems();
            while (rs.next()) {
                cuisineComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading cuisines: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void loadRestaurantData() {
        String query = "SELECT r.rest_id, r.name, c.name AS cuisine_name, r.image_path " +
                       "FROM Restaurants r JOIN Cuisines c ON r.cuisine_id = c.cuisine_id";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        tableModel.setRowCount(0); 
        
        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("rest_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("cuisine_name"));
                row.add(rs.getString("image_path"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading restaurants: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
    }

    private void addRestaurant() {
        String name = nameField.getText();
        String cuisineName = (String) cuisineComboBox.getSelectedItem();
        String imagePath = imagePathField.getText();

        if (name.isEmpty() || cuisineName == null) {
            JOptionPane.showMessageDialog(this, "Name and Cuisine are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cuisineId = getCuisineId(cuisineName);
        if (cuisineId == -1) return; 

        String query = "INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setInt(2, cuisineId);
            pst.setString(3, imagePath);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Restaurant added successfully.");
            loadRestaurantData(); 
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding restaurant: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }
    
    private void updateRestaurant() {
        if (selectedRestId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = nameField.getText();
        String cuisineName = (String) cuisineComboBox.getSelectedItem();
        String imagePath = imagePathField.getText();
        int cuisineId = getCuisineId(cuisineName);
        if (cuisineId == -1) return;

        String query = "UPDATE Restaurants SET name = ?, cuisine_id = ?, image_path = ? WHERE rest_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = AppConfig.getConnection();
            if (conn == null) return;
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setInt(2, cuisineId);
            pst.setString(3, imagePath);
            pst.setInt(4, selectedRestId);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Restaurant updated successfully.");
            loadRestaurantData(); 
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating restaurant: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, null);
        }
    }

    private void deleteRestaurant() {
        if (selectedRestId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Restaurant ID: " + selectedRestId + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM Restaurants WHERE rest_id = ?";
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = AppConfig.getConnection();
                if (conn == null) return;
                pst = conn.prepareStatement(query);
                pst.setInt(1, selectedRestId);
                
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Restaurant deleted successfully.");
                loadRestaurantData(); 
                clearFields();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting restaurant. Check for linked menu items/orders.", "DB Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                AppConfig.closeResources(conn, pst, null);
            }
        }
    }

    private int getCuisineId(String cuisineName) {
        String query = "SELECT cuisine_id FROM Cuisines WHERE name = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int id = -1;
        
        try {
            conn = AppConfig.getConnection();
            if (conn == null) return -1;
            pst = conn.prepareStatement(query);
            pst.setString(1, cuisineName);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                id = rs.getInt("cuisine_id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching Cuisine ID: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            AppConfig.closeResources(conn, pst, rs);
        }
        return id;
    }
    
}