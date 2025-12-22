package com.mycompany.multimealproject;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.EventQueue;

public class MultiMealProject {

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set System Look and Feel.");
        }

        EventQueue.invokeLater(() -> {
            // Application starts at the Splash Page
            new SplashPage().setVisible(true);
        });
    }
}