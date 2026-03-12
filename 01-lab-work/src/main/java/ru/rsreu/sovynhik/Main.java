package ru.rsreu.sovynhik;

import com.formdev.flatlaf.FlatLightLaf;
import ru.rsreu.sovynhik.cipher.Cipher;
import ru.rsreu.sovynhik.cipher.PermutationCipher;
import ru.rsreu.sovynhik.ui.PermutationCipherGUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        int[] permutation = {2, 5, 3, 4, 1, 6};
        Cipher cipher = new PermutationCipher(permutation);

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("defaultFont", defaultFont);
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            PermutationCipherGUI frame = new PermutationCipherGUI(cipher);
            frame.setVisible(true);
        });
    }
}
