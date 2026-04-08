package ru.rsreu.sovynhik;

import com.formdev.flatlaf.FlatLightLaf;
import ru.rsreu.sovynhik.gui.RSAGUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("defaultFont", defaultFont);
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            RSAGUI frame = new RSAGUI();
            frame.setVisible(true);
        });
    }
}