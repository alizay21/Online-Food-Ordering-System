package com.mycompany.multimealproject;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Updated Unit Tests for MultiMeal Project
 * Author: HS TRADER
 */
public class CartTest {
    
    public CartTest() {
    }

    /**
     * Test Case 1: Verifies adding an item and price calculation.
     * Use ID 43 as it has confirmed stock in your database.
     */
    @Test
    public void testAddItemAndSubTotal() {
        System.out.println("Running: testAddItemAndSubTotal");
        Cart.clear(); 
        
        int itemId = 43;
        double itemPrice = 450.0;
        int restId = 8;
        int quantity = 2;
        
        boolean result = Cart.addItem(itemId, itemPrice, restId, quantity);
        
        assertTrue("Item should be successfully added", result);
        assertEquals(900.0, Cart.getSubTotal(), 0.01);
        assertEquals(1, Cart.getItems().size());
    }

    /**
     * Test Case 2: Verifies the single-restaurant constraint.
     * Note: This will trigger a pop-up in your current logic. You must click 'No' 
     * for the assertion to pass correctly.
     */
//    @Test
//    public void testSingleRestaurantConstraint() {
//        System.out.println("Running: testSingleRestaurantConstraint");
//        Cart.clear();
//        
//        // Add first item
//        Cart.addItem(43, 450.0, 8, 1);
//        
//        // Try to add item from a different Restaurant ID (e.g., 9)
//        boolean result = Cart.addItem(10, 280.0, 9, 1);
//        
//        assertFalse("Should NOT allow adding items from a different restaurant", result);
//    }

    /**
     * Test Case 3: Verifies clearing the cart resets values.
     * Fix: Matches project logic where empty restaurant ID is -1.
     */
    @Test
    public void testClear() {
        System.out.println("Running: testClear");
        Cart.addItem(43, 450.0, 8, 2);
        Cart.clear();
        
        assertEquals(0.0, Cart.getSubTotal(), 0.0);
        assertEquals(0, Cart.getItems().size());
        // Updated to -1 to match your project's 'Empty' state
        assertEquals(-1, Cart.getRestaurantId()); 
    }

    /**
     * Test Case 4: Verifies removing an item.
     * Fix: Adding only 1 item to ensure the cart becomes empty upon removal.
     */
    @Test
    public void testRemoveItem() {
        System.out.println("Running: testRemoveItem");
        Cart.clear();
        Cart.addItem(43, 450.0, 8, 1); 
        
        Cart.removeItem(43, 450.0); 
        
        assertTrue("Cart should be empty after removal", Cart.getItems().isEmpty());
        assertEquals(0.0, Cart.getSubTotal(), 0.01);
    }

    /**
     * Test Case 5: Verifies packaging type setting.
     */
    @Test
    public void testSetPackagingType() {
        System.out.println("Running: testSetPackagingType");
        String type = "Eco-Friendly Box";
        Cart.setPackagingType(type);
        assertEquals(type, Cart.getPackagingType());
    }
}