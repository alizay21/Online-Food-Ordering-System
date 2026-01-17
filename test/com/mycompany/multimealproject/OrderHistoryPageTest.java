package com.mycompany.multimealproject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.*;

public class OrderHistoryPageTest {

    private static Connection testConnection;
    private final int testUserId = 999; 
    private final int testOrderId = 777; 
    private final int testRestaurantId = 8; 
    private final int testMenuItemId = 43; 

    @BeforeClass
    public static void setUpClass() {
        testConnection = AppConfig.getConnection();
        assertNotNull("Database connection failed.", testConnection);
    }

    @Before
    public void setUp() throws SQLException {
        if (testConnection == null || testConnection.isClosed()) {
            testConnection = AppConfig.getConnection();
        }
        cleanupTestData();
        insertFakeOrderData();
    }

    @After
    public void tearDown() throws SQLException {
        cleanupTestData();
    }

    @Test
    public void testRecommendationLogic() {
        System.out.println("Running: testRecommendationLogic");
        String sqlQuery = "SELECT mi.name AS item_name FROM Orders o " +
                          "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                          "JOIN Menu_Items mi ON oi.item_id = mi.item_id " +
                          "WHERE o.user_id = ? AND o.status != 'Cancelled' " +
                          "GROUP BY mi.item_id ORDER BY COUNT(o.order_id) DESC LIMIT 1";

        try (PreparedStatement preparedStatement = testConnection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, testUserId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue("Should find a recommendation", resultSet.next());
            assertEquals("Double Patty Burger", resultSet.getString("item_name")); 
        } catch (SQLException e) {
            fail("SQL Execution failed: " + e.getMessage());
        }
    }

    @Test
    public void testFeedbackCheck() throws SQLException {
        System.out.println("Running: testFeedbackCheck");
        int nonExistentOrderId = 8888;
        String sqlQuery = "SELECT 1 FROM Feedbacks WHERE order_id = ?";
        try (PreparedStatement preparedStatement = testConnection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, nonExistentOrderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertFalse("Should be false for non-existent feedback", resultSet.next());
        }
    }

    private void insertFakeOrderData() throws SQLException {
        // 1. Create User
        String userSql = "INSERT IGNORE INTO Users (user_id, username, password, email, role) " +
                         "VALUES (?, 'test_user_history', 'securePassword123', 'test@history.com', 'customer')";
        
        // 2. Create Order
        String orderSql = "INSERT INTO Orders (order_id, user_id, rest_id, total_amount, status, order_date) " +
                          "VALUES (?, ?, ?, 500.0, 'Delivered', NOW())";
        
        // 3. Create Order Item - UPDATED to include price_at_order and size_at_order
        String itemSql = "INSERT INTO Order_Items (order_id, item_id, quantity, price_at_order, size_at_order) " +
                          "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement userPst = testConnection.prepareStatement(userSql);
             PreparedStatement orderPst = testConnection.prepareStatement(orderSql);
             PreparedStatement itemPst = testConnection.prepareStatement(itemSql)) {
            
            userPst.setInt(1, testUserId);
            userPst.executeUpdate();
            
            orderPst.setInt(1, testOrderId);
            orderPst.setInt(2, testUserId);
            orderPst.setInt(3, testRestaurantId);
            orderPst.executeUpdate();
            
            // Fix: Adding the missing mandatory fields
            itemPst.setInt(1, testOrderId);
            itemPst.setInt(2, testMenuItemId);
            itemPst.setInt(3, 1); // quantity
            itemPst.setDouble(4, 450.0); // price_at_order
            itemPst.setString(5, "Small"); // size_at_order
            itemPst.executeUpdate();
        }
    }

    private void cleanupTestData() throws SQLException {
        try (Statement statement = testConnection.createStatement()) {
            statement.executeUpdate("DELETE FROM Order_Items WHERE order_id = " + testOrderId);
            statement.executeUpdate("DELETE FROM Orders WHERE order_id = " + testOrderId);
            statement.executeUpdate("DELETE FROM Users WHERE user_id = " + testUserId);
        }
    }
}