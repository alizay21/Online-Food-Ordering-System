package com.mycompany.multimealproject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.*;

public class LoginTest {

    private static Connection testConn;

    // Runs ONCE before any tests start
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Checking Database Connection...");
        testConn = AppConfig.getConnection();
        assertNotNull("Database should be connected before testing", testConn);
    }

    // Runs ONCE after all tests are done
    @AfterClass
    public static void tearDownClass() throws SQLException {
        if (testConn != null && !testConn.isClosed()) {
            testConn.close();
            System.out.println("Database connection closed.");
        }
    }

    // Runs before EACH test
    @Before
    public void setUp() {
        System.out.println("Preparing for next login test...");
    }

    @Test
    public void testValidAdminLogin() {
        // Using the logic from your LoginPage
        boolean result = performLoginCheck("admin_user", "admin123", "admin");
        assertTrue("Admin should be able to log in", result);
    }

    @Test
    public void testCaseSensitivity() {
        // Testing the BINARY keyword constraint in your SQL
        boolean result = performLoginCheck("admin", "ADMIN123", "admin");
        assertFalse("Login should fail due to wrong case", result);
    }

    private boolean performLoginCheck(String user, String pass, String role) {
        String query = "SELECT user_id FROM Users WHERE BINARY username = ? AND BINARY password = ? AND role = ?";
        try (PreparedStatement pst = testConn.prepareStatement(query)) {
            pst.setString(1, user);
            pst.setString(2, pass);
            pst.setString(3, role);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            return false;
        }
    }
}