package ru.rsreu.sovynhik.config;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class ThemeConfigurator {
    public static void setup() {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("defaultFont", defaultFont);

            UIManager.put("Button.arc", 12);
            UIManager.put("Button.background", new Color(70, 130, 180));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(100, 150, 220));
            UIManager.put("Button.pressedBackground", new Color(50, 100, 150));
            UIManager.put("Button.focusedBackground", new Color(70, 130, 180));
            UIManager.put("Button.focusWidth", 0);
            UIManager.put("Button.shadow", new Color(0, 0, 0, 40));

        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
        }
    }
}
