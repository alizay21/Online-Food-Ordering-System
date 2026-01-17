package com.mycompany.multimealproject;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Master Test Suite for MultiMealProject.
 * This runs all critical logic tests in one single execution.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AppConfigTest.class,         // 1. Check DB Connection first
    SignUpPageTest.class,            // 2. Check Registration logic
    LoginTest.class,             // 3. Check Authentication
    CartTest.class,              // 4. Check Shopping Cart math
    OrderHistoryPageTest.class,  // 5. Check History & Recommendations
    CheckoutPageTest.class       // 6. Check Final Transaction logic
})

public class MultiMealTestSuite {
    // This class remains empty. 
    // It only serves as a holder for the annotations above.
}