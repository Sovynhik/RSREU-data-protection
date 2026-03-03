package ru.rsreu.sovynhik.pract_04;

import com.formdev.flatlaf.FlatLightLaf;
import ru.rsreu.sovynhik.pract_04.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}