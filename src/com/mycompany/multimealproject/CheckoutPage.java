package com.mycompany.multimealproject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.Map;

public class CheckoutPage extends JPanel {

    private final MainAppFrame parentFrame;
    private final int userId;
    private final double deliveryCharge = 150.00;

    private JLabel totalSummaryLabel;
    private JTextField addressField;
    private JCheckBox saveAddressCheckbox;
    private JPanel itemSummaryPanel;
    private JButton confirmBtn;

    private JRadioButton cashRadio;
    private JRadioButton cardRadio;
    private ButtonGroup paymentGroup;

    public CheckoutPage(MainAppFrame parentFrame, int userId) {
        this.parentFrame = parentFrame;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(AppConfig.DARK_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainLayout(), BorderLayout.CENTER);
        loadAddressAndRefresh();
    }

    private void loadAddressAndRefresh() {
        String address = getAddressFromDB();
        addressField.setText(address.isEmpty() ? "" : address);
        refreshCartDisplay();
    }

    private String getAddressFromDB() {
        String query = "SELECT address FROM Users WHERE user_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("address") != null ? rs.getString("address") : "";
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching address: " + ex.getMessage());
        }
        return "";
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppConfig.CARD_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Checkout (" + Cart.getPackagingType() + ")", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(AppConfig.PRIMARY_RED);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createMainLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(AppConfig.DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(createItemDetailsPanel(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        mainPanel.add(createSummaryPanel(), gbc);

        return mainPanel;
    }

    private JPanel createItemDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(AppConfig.CARD_BACKGROUND);
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel addressInputPanel = new JPanel(new BorderLayout(5, 5));
        addressInputPanel.setBackground(AppConfig.CARD_BACKGROUND);

        JLabel addressLabelHeader = new JLabel("Delivery Address:");
        addressLabelHeader.setFont(AppConfig.FONT_BOLD);
        addressLabelHeader.setForeground(AppConfig.LIGHT_TEXT);

        addressField = new JTextField();
        addressField.setFont(AppConfig.FONT_INPUT);
        addressField.setBackground(AppConfig.DARK_BACKGROUND);
        addressField.setForeground(Color.WHITE);
        addressField.setCaretColor(Color.WHITE);
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConfig.BORDER_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        saveAddressCheckbox = new JCheckBox("Save this address for future orders");
        saveAddressCheckbox.setBackground(AppConfig.CARD_BACKGROUND);
        saveAddressCheckbox.setForeground(AppConfig.PLACEHOLDER_TEXT);
        saveAddressCheckbox.setSelected(true);

        addressInputPanel.add(addressLabelHeader, BorderLayout.NORTH);
        addressInputPanel.add(addressField, BorderLayout.CENTER);
        addressInputPanel.add(saveAddressCheckbox, BorderLayout.SOUTH);

        itemSummaryPanel = new JPanel();
        itemSummaryPanel.setLayout(new BoxLayout(itemSummaryPanel, BoxLayout.Y_AXIS));
        itemSummaryPanel.setBackground(AppConfig.CARD_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(itemSummaryPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppConfig.CARD_BACKGROUND);

        detailsPanel.add(addressInputPanel, BorderLayout.NORTH);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);
        return detailsPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(AppConfig.CARD_BACKGROUND);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pLabel = new JLabel("Select Payment Method:");
        pLabel.setFont(AppConfig.FONT_BOLD);
        pLabel.setForeground(AppConfig.LIGHT_TEXT);
        pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cashRadio = new JRadioButton("Cash on Delivery");
        cardRadio = new JRadioButton("Credit / Debit Card");
        styleRadio(cashRadio);
        styleRadio(cardRadio);
        cashRadio.setSelected(false);

        paymentGroup = new ButtonGroup();
        paymentGroup.add(cashRadio);
        paymentGroup.add(cardRadio);

        totalSummaryLabel = new JLabel("Total: Rs. 0.00");
        totalSummaryLabel.setFont(AppConfig.FONT_LARGE);
        totalSummaryLabel.setForeground(AppConfig.PRIMARY_RED);
        totalSummaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmBtn = new JButton("Place Order");
        confirmBtn.setBackground(AppConfig.PRIMARY_RED);
        confirmBtn.setForeground(new Color(30, 30, 30));
        confirmBtn.setFont(AppConfig.FONT_BOLD);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setContentAreaFilled(true);
        confirmBtn.setOpaque(true);
        confirmBtn.addActionListener(e -> placeOrder());

        summaryPanel.add(pLabel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(cashRadio);
        summaryPanel.add(cardRadio);
        summaryPanel.add(Box.createVerticalStrut(30));
        summaryPanel.add(totalSummaryLabel);
        summaryPanel.add(Box.createVerticalStrut(20));
        summaryPanel.add(confirmBtn);

        return summaryPanel;
    }

    private void styleRadio(JRadioButton rb) {
        rb.setBackground(AppConfig.CARD_BACKGROUND);
        rb.setForeground(AppConfig.LIGHT_TEXT);
        rb.setFocusPainted(false);
        rb.setFont(AppConfig.FONT_REGULAR);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void refreshCartDisplay() {
        itemSummaryPanel.removeAll();
        if (Cart.getItems().isEmpty()) {
            confirmBtn.setEnabled(false);
            totalSummaryLabel.setText("Total: Rs. 0.00");
        } else {
            loadCartDetails();
            confirmBtn.setEnabled(true);
            double finalTotal = Cart.getSubTotal() + deliveryCharge;
            totalSummaryLabel.setText("Total: Rs. " + String.format("%.2f", finalTotal));
        }
        itemSummaryPanel.revalidate();
        itemSummaryPanel.repaint();
    }

    private void loadCartDetails() {
        Map<Integer, Integer> cartItems = Cart.getItems();
        if (cartItems.isEmpty()) return;

        String itemIds = cartItems.keySet().toString().replace("[", "").replace("]", "");
        String query = "SELECT item_id, name, price FROM Menu_Items WHERE item_id IN (" + itemIds + ")";

        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                double price = rs.getDouble("price");
                itemSummaryPanel.add(createItemRow(itemId, rs.getString("name"), cartItems.get(itemId), price));
            }
            itemSummaryPanel.add(Box.createVerticalStrut(20));
            itemSummaryPanel.add(createPriceRow("Sub Total:", Cart.getSubTotal()));
            itemSummaryPanel.add(createPriceRow("Delivery Fee:", deliveryCharge));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createItemRow(int itemId, String name, int quantity, double price) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(AppConfig.CARD_BACKGROUND);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(AppConfig.LIGHT_TEXT);
        nameLabel.setFont(AppConfig.FONT_BOLD);

        JLabel qtyLabel = new JLabel(" x" + quantity + " ");
        qtyLabel.setForeground(AppConfig.PRIMARY_RED);

        JLabel priceLabel = new JLabel("Rs. " + String.format("%.2f", quantity * price));
        priceLabel.setForeground(AppConfig.LIGHT_TEXT);

        // Styling helper for the small buttons
        JButton minusBtn = createStyledSmallButton("-");
        minusBtn.addActionListener(e -> {
            Cart.removeItem(itemId, price);
            loadAddressAndRefresh();
        });

        JButton plusBtn = createStyledSmallButton("+");
        plusBtn.addActionListener(e -> {
            Cart.addItem(itemId, price, Cart.getRestaurantId(), 1);
            loadAddressAndRefresh();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        controls.setBackground(AppConfig.CARD_BACKGROUND);
        controls.add(minusBtn);
        controls.add(plusBtn);
        controls.add(qtyLabel);
        controls.add(priceLabel);

        row.add(nameLabel, BorderLayout.WEST);
        row.add(controls, BorderLayout.EAST);
        return row;
    }

    // NEW HELPER: Ensures buttons are colored and visible
    private JButton createStyledSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(35, 28));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Match the "Place Order" button colors for visibility
        btn.setBackground(AppConfig.PRIMARY_RED);
        btn.setForeground(new Color(30, 30, 30)); // Dark text on Red background
        
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private JLabel createPriceRow(String label, double amount) {
        JLabel row = new JLabel(label + " Rs. " + String.format("%.2f", amount));
        row.setForeground(AppConfig.PLACEHOLDER_TEXT);
        return row;
    }

    private void placeOrder() {
        String address = addressField.getText().trim();
        String paymentMethod = null;

           if (cashRadio.isSelected()) {
               paymentMethod = "Cash";
           } else if (cardRadio.isSelected()) {
               paymentMethod = "Card";
           } else {
               JOptionPane.showMessageDialog(this, "Please select a payment method.");
               return;
           }


        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an address.");
            return;
        }

        if (paymentMethod.equals("Card")) {
            String card = JOptionPane.showInputDialog(this, "Enter 16-digit Card Number:");
            if (card == null) return;
            if (card.replaceAll("\\D", "").length() != 16) {
                JOptionPane.showMessageDialog(this, "Invalid Card Number.");
                return;
            }
        }

        if (!Cart.subtractStockOnCheckout()) return;

        int userOrderSequence = 1;
        try (Connection conn = AppConfig.getConnection()) {
            // Count user's previous orders
            String countQuery = "SELECT COUNT(*) FROM Orders WHERE user_id = ?";
            PreparedStatement pstCount = conn.prepareStatement(countQuery);
            pstCount.setInt(1, userId);
            ResultSet rsCount = pstCount.executeQuery();
            if (rsCount.next()) {
                userOrderSequence = rsCount.getInt(1) + 1;
            }

            conn.setAutoCommit(false);
            String orderQuery = "INSERT INTO Orders (user_id, rest_id, total_amount, delivery_address, status, payment_method) VALUES (?, ?, ?, ?, 'Pending', ?)";
            PreparedStatement pstOrder = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            pstOrder.setInt(1, userId);
            pstOrder.setInt(2, Cart.getRestaurantId());
            pstOrder.setDouble(3, Cart.getSubTotal() + deliveryCharge);
            pstOrder.setString(4, address);
            pstOrder.setString(5, paymentMethod);
            pstOrder.executeUpdate();

            ResultSet rsKeys = pstOrder.getGeneratedKeys();
            if (rsKeys.next()) {
                long globalOrderId = rsKeys.getLong(1);
                String itemQuery = "INSERT INTO Order_Items (order_id, item_id, quantity, price_at_order) SELECT ?, item_id, ?, price FROM Menu_Items WHERE item_id = ?";
                PreparedStatement pstItem = conn.prepareStatement(itemQuery);
                for (Map.Entry<Integer, Integer> entry : Cart.getItems().entrySet()) {
                    pstItem.setLong(1, globalOrderId);
                    pstItem.setInt(2, entry.getValue());
                    pstItem.setInt(3, entry.getKey());
                    pstItem.addBatch();
                }
                pstItem.executeBatch();
                conn.commit();

                if (saveAddressCheckbox.isSelected()) saveAddressToProfile(address);

                JOptionPane.showMessageDialog(this, "Your Order #" + userOrderSequence + " placed successfully!");
                Cart.clear();
                parentFrame.switchView(MainAppFrame.HOME_VIEW);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Checkout error: " + ex.getMessage());
        }
    }

    private void saveAddressToProfile(String address) {
        String query = "UPDATE Users SET address = ? WHERE user_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, address);
            pst.setInt(2, userId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}