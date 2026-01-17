package com.mycompany.multimealproject;

import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Connection;
import javax.swing.ImageIcon;

/**
 * Tests the core configuration and connection logic of the MultiMeal app.
 */
public class AppConfigTest {

    /**
     * Test Case 1: Database Connection
     * Verifies that the app can successfully reach the 'multimeal_db' on MySQL.
     */
    @Test
    public void testDatabaseConnection() {
        System.out.println("Running: testDatabaseConnection");
        Connection conn = AppConfig.getConnection();
        
        // If this is null, your XAMPP/WAMP is off or the DB name is wrong
        assertNotNull("Connection should not be null. Check XAMPP and DB name.", conn);
        
        // Clean up the connection after testing
        AppConfig.closeResources(conn, null, null);
    }

    /**
     * Test Case 2: Image Loading with Null Path
     * Ensures the app handles missing images gracefully instead of crashing.
     */
    @Test
    public void testLoadImageWithNullPath() {
        System.out.println("Running: testLoadImageWithNullPath");
        ImageIcon icon = AppConfig.loadImage(null, 100, 100);
        assertNull("Should return null for a null path without crashing", icon);
    }

    /**
     * Test Case 3: Color Constants
     * Ensures the design theme remains consistent.
     */
    @Test
    public void testThemeConstants() {
        System.out.println("Running: testThemeConstants");
        // Verify the background color is the dark gray you intended
        assertNotNull(AppConfig.DARK_BACKGROUND);
        assertEquals(24, AppConfig.DARK_BACKGROUND.getRed());
    }
}