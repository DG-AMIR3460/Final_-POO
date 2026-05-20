package main;

import com.formdev.flatlaf.FlatDarkLaf;
import core.view.LoginView;

import javax.swing.*;

/**
 * Punto de entrada de la aplicacion.
 * Lanza la vista de Login (LoginView).
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf theme: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
