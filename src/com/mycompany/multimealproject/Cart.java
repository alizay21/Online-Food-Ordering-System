package com.mycompany.multimealproject;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import java.sql.*;

public class Cart {

    private static Map<Integer, Integer> cartItems = new HashMap<>();
    private static int restaurantId = -1; 
    private static double subTotal = 0.0;
    private static String packagingType = "Standard";
    
    public static Map<Integer, Integer> getItems() { return cartItems; }
    public static double getSubTotal() { return subTotal; }
    public static String getPackagingType() { return packagingType; }
    public static int getRestaurantId() { return restaurantId; }
    
    public static void setPackagingType(String type) { packagingType = type; }
    
    public static boolean addItem(int itemId, double itemPrice, int restId, int quantity) {
        if (restaurantId != -1 && restaurantId != restId) {
            int confirm = JOptionPane.showConfirmDialog(null, "Clear cart to add from new restaurant?", "Change Restaurant", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) clear();
            else return false;
        }

        int stock = checkItemStockInDB(itemId);
        int currentQty = cartItems.getOrDefault(itemId, 0);
        if (currentQty + quantity > stock) {
            JOptionPane.showMessageDialog(null, "Not enough stock. Available: " + stock);
            return false;
        }

        restaurantId = restId;
        cartItems.put(itemId, currentQty + quantity);
        subTotal += itemPrice * quantity;
        return true;
    }

    public static void removeItem(int itemId, double itemPrice) {
        if (cartItems.containsKey(itemId)) {
            int qty = cartItems.get(itemId);
            subTotal -= itemPrice;
            if (qty <= 1) cartItems.remove(itemId);
            else cartItems.put(itemId, qty - 1);
            if (cartItems.isEmpty()) clear();
        }
    }

    public static void clear() {
        cartItems.clear();
        restaurantId = -1;
        subTotal = 0.0;
        packagingType = "Standard";
    }

    private static int checkItemStockInDB(int itemId) {
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT stock FROM Menu_Items WHERE item_id = ?")) {
            pst.setInt(1, itemId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("stock");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    public static boolean subtractStockOnCheckout() {
        Connection conn = null;
        try {
            conn = AppConfig.getConnection();
            conn.setAutoCommit(false);
            String updateQuery = "UPDATE Menu_Items SET stock = stock - ? WHERE item_id = ? AND stock >= ?";
            PreparedStatement pst = conn.prepareStatement(updateQuery);
            
            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                pst.setInt(1, entry.getValue());
                pst.setInt(2, entry.getKey());
                pst.setInt(3, entry.getValue());
                pst.addBatch();
            }
            
            int[] results = pst.executeBatch();
            for (int r : results) {
                if (r == 0) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Stock mismatch. Please refresh cart.");
                    return false;
                }
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception e) {}
            return false;
        }
    }
}