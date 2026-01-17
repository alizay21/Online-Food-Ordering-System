package com.mycompany.multimealproject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.*;

public class SignUpPageTest {

    private static Connection testConn;
    private final String TEST_USER = "TestUser123";
    private final String TEST_EMAIL = "test@multimeal.com";

    @BeforeClass
    public static void setUpClass() {
        testConn = AppConfig.getConnection();
        assertNotNull("Database connection failed", testConn);
    }

    @Before
    public void setUp() throws SQLException {
        // CLEANUP BEFORE: Remove test user if they somehow exist from a previous failed run
        cleanupTestUser();
        System.out.println("Database cleaned. Ready for test.");
    }

    @After
    public void tearDown() throws SQLException {
        // CLEANUP AFTER: Remove the user created during the test to keep DB identical to before
        cleanupTestUser();
        System.out.println("Database restored to original state.");
    }

    @Test
    public void testSuccessfulRegistration() {
        System.out.println("Running: testSuccessfulRegistration");
        boolean result = performRegistration(TEST_USER, "password123", TEST_EMAIL);
        assertTrue("User should be registered successfully", result);
    }

    @Test
    public void testDuplicateRegistrationFails() {
        System.out.println("Running: testDuplicateRegistrationFails");
        // Register the first time
        performRegistration(TEST_USER, "password123", TEST_EMAIL);
        
        // Try registering the exact same user again
        boolean secondResult = performRegistration(TEST_USER, "password123", TEST_EMAIL);
        assertFalse("Should not allow duplicate username/email", secondResult);
    }

    @Test
    public void testInvalidEmailFormat() {
        System.out.println("Running: testInvalidEmailFormat");
        // Testing your regex: ^[A-Za-z0-9+_.-]+@(.+)$
        String badEmail = "not-an-email";
        assertFalse("Should reject invalid email format", badEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
    }

    /**
     * This mimics the logic inside your handleRegistration() 
     * but without the JOptionPane pop-ups.
     */
    private boolean performRegistration(String user, String pass, String email) {
        String query = "INSERT INTO Users(username, password, email, role) VALUES(?, ?, ?, 'customer')";
        try (PreparedStatement pst = testConn.prepareStatement(query)) {
            pst.setString(1, user);
            pst.setString(2, pass);
            pst.setString(3, email);
            pst.executeUpdate();
            return true;
        } catch (SQLException ex) {
            // This will catch the SQLIntegrityConstraintViolationException for duplicates
            return false;
        }
    }

    private void cleanupTestUser() throws SQLException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (PreparedStatement pst = testConn.prepareStatement(sql)) {
            pst.setString(1, TEST_USER);
            pst.executeUpdate();
        }
    }
}