package com.mycompany.multimealproject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.*;


/**
 * Unit tests for the Checkout logic.
 * Verifies address handling, total calculation, and order insertion.
 */
public class CheckoutPageTest {

    private static Connection testConnection;
    private final int testUserId = 999;
    private final double deliveryCharge = 150.00;

    @BeforeClass
    public static void setUpClass() {
        testConnection = AppConfig.getConnection();
        assertNotNull("Database must be connected for checkout tests", testConnection);
    }

    @Before
    public void setUp() throws SQLException {
        if (testConnection == null || testConnection.isClosed()) {
            testConnection = AppConfig.getConnection();
        }
        Cart.clear();
        cleanupTestData();
        // Insert a test user so foreign keys don't fail
        insertTestUser();
    }

    @After
    public void tearDown() throws SQLException {
        cleanupTestData();
    }

    /**
     * Test Case 1: Verifies the final total calculation.
     * Logic: (Subtotal from Cart) + Delivery Charge
     */
    @Test
    public void testFinalTotalCalculation() {
        System.out.println("Running: testFinalTotalCalculation");
        
        // Setup: Add item worth 450.0
        Cart.addItem(43, 450.0, 8, 2); // 450 * 2 = 900
        
        double expectedTotal = 900.0 + deliveryCharge; // 1050.0
        double actualTotal = Cart.getSubTotal() + deliveryCharge;
        
        assertEquals("Total should be Subtotal + Delivery Charge", expectedTotal, actualTotal, 0.01);
    }

    /**
     * Test Case 2: Verifies saving address to user profile.
     * Mimics the saveAddressToProfile() method.
     */
    @Test
    public void testSaveAddressToProfile() throws SQLException {
        System.out.println("Running: testSaveAddressToProfile");
        String testAddress = "123 Test Street, Automation City";
        
        String query = "UPDATE Users SET address = ? WHERE user_id = ?";
        try (PreparedStatement pst = testConnection.prepareStatement(query)) {
            pst.setString(1, testAddress);
            pst.setInt(2, testUserId);
            int rowsAffected = pst.executeUpdate();
            
            assertEquals("One row should be updated in the Users table", 1, rowsAffected);
        }
        
        // Verify the data was actually saved
        String checkQuery = "SELECT address FROM Users WHERE user_id = ?";
        try (PreparedStatement pst = testConnection.prepareStatement(checkQuery)) {
            pst.setInt(1, testUserId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                assertEquals(testAddress, rs.getString("address"));
            }
        }
    }

    /**
     * Test Case 3: Verifies Order Sequence Logic.
     * Mimics the logic that determines "Your Order #X placed successfully".
     */
    @Test
    public void testOrderSequenceCalculation() throws SQLException {
        System.out.println("Running: testOrderSequenceCalculation");
        
        // 1. Manually insert one order for our test user
        String insertOrder = "INSERT INTO Orders (user_id, rest_id, total_amount, status) VALUES (?, 8, 500.0, 'Pending')";
        try (PreparedStatement pst = testConnection.prepareStatement(insertOrder)) {
            pst.setInt(1, testUserId);
            pst.executeUpdate();
        }
        
        // 2. Run the sequence logic from placeOrder()
        int userOrderSequence = 1;
        String countQuery = "SELECT COUNT(*) FROM Orders WHERE user_id = ?";
        try (PreparedStatement pstCount = testConnection.prepareStatement(countQuery)) {
            pstCount.setInt(1, testUserId);
            ResultSet rsCount = pstCount.executeQuery();
            if (rsCount.next()) {
                userOrderSequence = rsCount.getInt(1) + 1;
            }
        }
        
        assertEquals("The next order should be sequence #2", 2, userOrderSequence);
    }

    private void insertTestUser() throws SQLException {
        String sql = "INSERT IGNORE INTO Users (user_id, username, password, email, role) " +
                     "VALUES (?, 'checkout_tester', 'password123', 'check@test.com', 'customer')";
        try (PreparedStatement pst = testConnection.prepareStatement(sql)) {
            pst.setInt(1, testUserId);
            pst.executeUpdate();
        }
    }

    private void cleanupTestData() throws SQLException {
        // Delete items first, then orders, then user to maintain integrity
        testConnection.createStatement().executeUpdate("DELETE FROM Order_Items WHERE order_id IN (SELECT order_id FROM Orders WHERE user_id = " + testUserId + ")");
        testConnection.createStatement().executeUpdate("DELETE FROM Orders WHERE user_id = " + testUserId);
        testConnection.createStatement().executeUpdate("DELETE FROM Users WHERE user_id = " + testUserId);
    }
}