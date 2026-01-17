package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailsModal extends JDialog {

    private final MainAppFrame parentFrame;
    private final int userId;
    private final int restId;
    private final String itemName;
    private String itemImagePath;
    private String itemDescription;
    private double packagingFee = 0.0;

    // UI Components
    private JComboBox<String> sizeComboBox;
    private JSpinner quantitySpinner;
    private JLabel currentPriceLabel;
    private JLabel stock_amountLabel;
    private JLabel totalLabel;
    private JRadioButton standardPackagingRadio;
    private JRadioButton ecoPackagingRadio;
    private JButton addToCartButton;

    private Map<String, Map<String, Object>> itemVariants = new HashMap<>();

    public ItemDetailsModal(JFrame parent, MainAppFrame parentFrame, int userId, int restId, String itemName,
            String imagePath, String description) {
        super(parent, "Details: " + itemName, true);
        this.parentFrame = parentFrame;
        this.userId = userId;
        this.restId = restId;
        this.itemName = itemName;
        this.itemImagePath = imagePath;
        this.itemDescription = description;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(parent);

        if (!loadItemVariants()) {
            JOptionPane.showMessageDialog(parent, "Could not load item details.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initComponents();

        sizeComboBox.addActionListener(e -> updatePriceAndStock());
        updatePriceAndStock(); 
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- WEST: Image ---
        JLabel imageLabel = new JLabel("", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(180, 180));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(AppConfig.DARK_BACKGROUND);
        imageLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
        ImageIcon icon = AppConfig.loadImage(itemImagePath, 180, 180);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("[Image]");
        }
        mainPanel.add(imageLabel, BorderLayout.WEST);

        // --- CENTER: Details and Selection ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(AppConfig.DARK_BACKGROUND);

        JLabel title = new JLabel(itemName, JLabel.LEFT);
        title.setFont(AppConfig.FONT_LARGE);
        title.setForeground(AppConfig.PRIMARY_RED);
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(15));

        sizeComboBox = new JComboBox<>(itemVariants.keySet().toArray(new String[0]));
        sizeComboBox.setFont(AppConfig.FONT_REGULAR);
        sizeComboBox.setPreferredSize(new Dimension(200, 30));
        sizeComboBox.setMaximumSize(new Dimension(200, 30));
        centerPanel.add(createLabeledControl("Select Size:", sizeComboBox));
        centerPanel.add(Box.createVerticalStrut(10));

        currentPriceLabel = new JLabel("Price: Rs. 0.00");
        currentPriceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentPriceLabel.setForeground(AppConfig.LIGHT_TEXT);
        centerPanel.add(currentPriceLabel);

        stock_amountLabel = new JLabel("Stock: --");
        stock_amountLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        stock_amountLabel.setForeground(AppConfig.PLACEHOLDER_TEXT);
        centerPanel.add(stock_amountLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // --- QUANTITY SPINNER (FIXED) ---
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(AppConfig.FONT_REGULAR);
        quantitySpinner.setPreferredSize(new Dimension(100, 30));
        quantitySpinner.setMaximumSize(new Dimension(100, 30));
        
        // FIX: Make the spinner text field non-editable
        JComponent editor = quantitySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false); // Prevents users from typing manual text like "sdf"
            textField.setBackground(AppConfig.DARK_BACKGROUND);
            textField.setForeground(Color.WHITE);
        }

        quantitySpinner.addChangeListener(e -> recalcTotal()); 
        centerPanel.add(createLabeledControl("Select Quantity:", quantitySpinner));
        centerPanel.add(Box.createVerticalStrut(10));

        // Packaging Options
        JPanel packagingPanel = new JPanel(new BorderLayout(10, 5));
        packagingPanel.setBackground(AppConfig.DARK_BACKGROUND);
        JLabel packagingLabel = new JLabel("Packaging:");
        packagingLabel.setForeground(AppConfig.LIGHT_TEXT);
        packagingLabel.setFont(AppConfig.FONT_BOLD);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        radioPanel.setBackground(AppConfig.DARK_BACKGROUND);

        standardPackagingRadio = new JRadioButton("Standard (Free)");
        ecoPackagingRadio = new JRadioButton("Eco-Friendly (+Rs. " + String.format("%.2f", packagingFee) + ")");
        standardPackagingRadio.setBackground(AppConfig.DARK_BACKGROUND);
        ecoPackagingRadio.setBackground(AppConfig.DARK_BACKGROUND);
        standardPackagingRadio.setForeground(AppConfig.LIGHT_TEXT);
        ecoPackagingRadio.setForeground(AppConfig.LIGHT_TEXT);
        standardPackagingRadio.setFont(AppConfig.FONT_REGULAR);
        ecoPackagingRadio.setFont(AppConfig.FONT_REGULAR);
        standardPackagingRadio.setFocusPainted(false);
        ecoPackagingRadio.setFocusPainted(false);

        standardPackagingRadio.addActionListener(e -> recalcTotal());
        ecoPackagingRadio.addActionListener(e -> recalcTotal());

        ButtonGroup group = new ButtonGroup();
        group.add(standardPackagingRadio);
        group.add(ecoPackagingRadio);
        standardPackagingRadio.setSelected(true);

        radioPanel.add(standardPackagingRadio);
        radioPanel.add(ecoPackagingRadio);

        packagingPanel.add(packagingLabel, BorderLayout.NORTH);
        packagingPanel.add(radioPanel, BorderLayout.CENTER);
        centerPanel.add(packagingPanel);

        totalLabel = new JLabel("Total: Rs. 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(AppConfig.PRIMARY_RED);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(totalLabel);

        // --- SOUTH: Button ---
        addToCartButton = new JButton("Add to Cart");
        AppConfig.styleFlatButton(addToCartButton, AppConfig.PRIMARY_RED, Color.WHITE);
        addToCartButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        addToCartButton.addActionListener(e -> handleAddToCart());

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(addToCartButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createLabeledControl(String labelText, JComponent control) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(AppConfig.DARK_BACKGROUND);
        JLabel label = new JLabel(labelText);
        label.setForeground(AppConfig.LIGHT_TEXT);
        label.setFont(AppConfig.FONT_BOLD);
        panel.add(label, BorderLayout.WEST);
        panel.add(control, BorderLayout.CENTER);
        return panel;
    }

    private boolean loadItemVariants() {
        String query = "SELECT item_id, size, price, stock_amount, description, image_path, packaging_fee FROM Menu_Items WHERE rest_id = ? AND name = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, restId);
            pst.setString(2, itemName);
            ResultSet rs = pst.executeQuery();

            boolean first = true;
            while (rs.next()) {
                String size = rs.getString("size");
                Map<String, Object> details = new HashMap<>();
                details.put("item_id", rs.getInt("item_id"));
                details.put("price", rs.getDouble("price"));
                details.put("stock_amount", rs.getInt("stock_amount"));
                itemVariants.put(size, details);

                if (first) {
                    first = false;
                    if (itemDescription == null || itemDescription.trim().isEmpty()) {
                        itemDescription = rs.getString("description");
                    }
                    if (itemImagePath == null || itemImagePath.trim().isEmpty()) {
                        itemImagePath = rs.getString("image_path");
                    }
                    packagingFee = rs.getDouble("packaging_fee");
                }
            }
            return !itemVariants.isEmpty();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void updatePriceAndStock() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        if (selectedSize == null) return;

        Map<String, Object> details = itemVariants.get(selectedSize);
        if (details == null) return;

        double price = (double) details.get("price");
        int stock_amount = (int) details.get("stock_amount");

        currentPriceLabel.setText("Price: Rs. " + String.format("%.2f", price));
        stock_amountLabel.setText("Stock: " + (stock_amount > 0 ? stock_amount + " available" : "SOLD OUT"));

        SpinnerNumberModel model = (SpinnerNumberModel) quantitySpinner.getModel();
        if (stock_amount > 0) {
            model.setMaximum(stock_amount);
            if ((Integer) model.getValue() > stock_amount) {
                model.setValue(stock_amount);
            }
            if ((Integer) model.getValue() == 0) {
                model.setValue(1);
            }
            addToCartButton.setEnabled(true);
        } else {
            model.setValue(0);
            model.setMaximum(0);
            addToCartButton.setEnabled(false);
        }
        recalcTotal();
    }

    private void recalcTotal() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        if (selectedSize == null) return;
        Map<String, Object> details = itemVariants.get(selectedSize);
        if (details == null) return;
        
        double basePrice = (double) details.get("price");
        int quantity = (Integer) quantitySpinner.getValue();
        double extra = (ecoPackagingRadio != null && ecoPackagingRadio.isSelected()) ? packagingFee : 0.0;
        double total = (basePrice + extra) * quantity;
        
        totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
        if (ecoPackagingRadio != null) {
            ecoPackagingRadio.setText("Eco-Friendly (+" + String.format("%.2f", packagingFee) + " / item)");
        }
    }

    private void handleAddToCart() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();

        if (quantity <= 0 || selectedSize == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> details = itemVariants.get(selectedSize);
        int itemId = (int) details.get("item_id");
        double basePrice = (double) details.get("price");
        String selectedPackaging = (ecoPackagingRadio != null && ecoPackagingRadio.isSelected()) ? "Eco-Friendly" : "Standard";
        double perUnitPrice = basePrice + ("Eco-Friendly".equals(selectedPackaging) ? packagingFee : 0.0);

        Cart.setPackagingType(selectedPackaging);
        boolean added = Cart.addItem(itemId, perUnitPrice, restId, quantity);

        if (added) {
            JOptionPane.showMessageDialog(this, quantity + " x " + itemName + " (" + selectedSize + ") added to cart!");
            dispose();
            CheckoutPage checkoutView = new CheckoutPage(parentFrame, userId);
            parentFrame.getContentPanel().add(checkoutView, MainAppFrame.CHECKOUT_VIEW);
            parentFrame.switchView(MainAppFrame.MENU_VIEW);
           
        }
    }
}