package ru.rsreu.sovynhik;

import ru.rsreu.sovynhik.config.ThemeConfigurator;
import ru.rsreu.sovynhik.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ThemeConfigurator.setup();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}