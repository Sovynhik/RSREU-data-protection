package ru.rsreu.sovynhik.ui;

import ru.rsreu.sovynhik.cipher.FeistelCipher;
import ru.rsreu.sovynhik.utils.ByteUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class MainFrame extends JFrame {
    private static final int ROUNDS = 24; // для варианта 9

    private final JTextArea taOriginal = new JTextArea(5, 40);
    private final JTextField tfKey = new JTextField("ключ_9", 20);
    private final JTextArea taEncryptedHex = new JTextArea(3, 40);
    private final JTextArea taDecrypted = new JTextArea(3, 40);

    public MainFrame() {
        setTitle("Лабораторная работа №3 - Сеть Фейштеля (Вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Панель исходного текста
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Исходный текст"));
        taOriginal.setFont(new Font("Monospaced", Font.PLAIN, 14));
        taOriginal.setLineWrap(true);
        taOriginal.setWrapStyleWord(true);
        topPanel.add(new JScrollPane(taOriginal), BorderLayout.CENTER);

        // Панель ключа
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Ключ (до 8 символов)"));
        keyPanel.add(new JLabel("Ключ:"));
        keyPanel.add(tfKey);
        JButton btnLoadExample = new JButton("Загрузить пример");
        btnLoadExample.addActionListener(e -> loadExample());
        keyPanel.add(btnLoadExample);

        // Панель кнопок шифрования/дешифрования
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnEncrypt = new JButton("Зашифровать");
        btnEncrypt.addActionListener(e -> encrypt());
        JButton btnDecrypt = new JButton("Расшифровать");
        btnDecrypt.addActionListener(e -> decrypt());
        JButton btnClear = new JButton("Очистить");
        btnClear.addActionListener(e -> clearAll());
        actionPanel.add(btnEncrypt);
        actionPanel.add(btnDecrypt);
        actionPanel.add(btnClear);

        // Панель результатов
        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        taEncryptedHex.setEditable(false);
        taEncryptedHex.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taEncryptedHex.setLineWrap(true);
        taEncryptedHex.setWrapStyleWord(true);
        JScrollPane scrollHex = new JScrollPane(taEncryptedHex);
        scrollHex.setBorder(BorderFactory.createTitledBorder("Зашифровано (HEX)"));

        taDecrypted.setEditable(false);
        taDecrypted.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taDecrypted.setLineWrap(true);
        taDecrypted.setWrapStyleWord(true);
        JScrollPane scrollDec = new JScrollPane(taDecrypted);
        scrollDec.setBorder(BorderFactory.createTitledBorder("Расшифровано"));

        resultPanel.add(scrollHex);
        resultPanel.add(scrollDec);

        // Сборка
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.CENTER);
        northPanel.add(keyPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadExample() {
        taOriginal.setText("Пример текста для шифрования. Сеть Фейштеля.");
        tfKey.setText("ключ_9");
    }

    private void clearAll() {
        taOriginal.setText("");
        tfKey.setText("");
        taEncryptedHex.setText("");
        taDecrypted.setText("");
    }

    private void encrypt() {
        try {
            String originalText = taOriginal.getText();
            if (originalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите исходный текст.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String keyStr = tfKey.getText();
            byte[] keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);

            FeistelCipher cipher = new FeistelCipher(keyBytes, ROUNDS);
            byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.encrypt(originalBytes);
            String hex = ByteUtils.bytesToHex(encryptedBytes);
            taEncryptedHex.setText(hex);

            // Очистить предыдущий результат расшифровки
            taDecrypted.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decrypt() {
        try {
            String hex = taEncryptedHex.getText().replaceAll("\\s", "");
            if (hex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет зашифрованных данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            byte[] encryptedBytes = ByteUtils.hexToBytes(hex);
            String keyStr = tfKey.getText();
            byte[] keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);

            FeistelCipher cipher = new FeistelCipher(keyBytes, ROUNDS);
            byte[] decryptedBytes = cipher.decrypt(encryptedBytes);

            // Убираем лишние нулевые байты в конце (дополнение)
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8).trim();
            taDecrypted.setText(decryptedText);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка дешифрования: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
