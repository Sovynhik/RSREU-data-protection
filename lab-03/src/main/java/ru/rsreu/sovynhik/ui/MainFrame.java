package ru.rsreu.sovynhik.ui;

import ru.rsreu.sovynhik.cipher.FeistelCipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private final JTextArea taOriginal = new JTextArea(5, 40);
    private final JTextField tfKey = new JTextField("ключ_9", 20);
    private final JTextField tfRounds = new JTextField("24", 5);
    private final JTextArea taEncryptedHex = new JTextArea(3, 40);
    private final JTextArea taDecrypted = new JTextArea(3, 40);

    public MainFrame() {
        setTitle("Лабораторная работа №3 - Сеть Фейштеля (Вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Исходные данные"));

        taOriginal.setFont(new Font("Monospaced", Font.PLAIN, 14));
        taOriginal.setLineWrap(true);
        taOriginal.setWrapStyleWord(true);
        JScrollPane scrollOriginal = new JScrollPane(taOriginal);
        scrollOriginal.setBorder(BorderFactory.createTitledBorder("Текст для шифрования"));
        topPanel.add(scrollOriginal, BorderLayout.CENTER);

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("Ключ (до 8 символов):"));
        keyPanel.add(tfKey);
        keyPanel.add(Box.createHorizontalStrut(10));
        keyPanel.add(new JLabel("Раундов:"));
        keyPanel.add(tfRounds);
        JButton btnLoadExample = new JButton("Загрузить пример");
        btnLoadExample.addActionListener(this::loadExample);
        keyPanel.add(btnLoadExample);
        topPanel.add(keyPanel, BorderLayout.SOUTH);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnEncrypt = new JButton("Шифровать");
        JButton btnDecrypt = new JButton("Дешифровать");
        JButton btnStepEncrypt = new JButton("Пошагово шифрование");
        JButton btnStepDecrypt = new JButton("Пошагово дешифрование");
        JButton btnClear = new JButton("Очистить всё");

        btnEncrypt.addActionListener(this::encrypt);
        btnDecrypt.addActionListener(this::decrypt);
        btnStepEncrypt.addActionListener(this::stepEncrypt);
        btnStepDecrypt.addActionListener(this::stepDecrypt);
        btnClear.addActionListener(this::clearAll);

        actionPanel.add(btnEncrypt);
        actionPanel.add(btnDecrypt);
        actionPanel.add(btnStepEncrypt);
        actionPanel.add(btnStepDecrypt);
        actionPanel.add(btnClear);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        taEncryptedHex.setEditable(false);
        taEncryptedHex.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taEncryptedHex.setLineWrap(true);
        taEncryptedHex.setWrapStyleWord(true);
        JScrollPane scrollEnc = new JScrollPane(taEncryptedHex);
        scrollEnc.setBorder(BorderFactory.createTitledBorder("Зашифровано (HEX)"));

        taDecrypted.setEditable(false);
        taDecrypted.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taDecrypted.setLineWrap(true);
        taDecrypted.setWrapStyleWord(true);
        JScrollPane scrollDec = new JScrollPane(taDecrypted);
        scrollDec.setBorder(BorderFactory.createTitledBorder("Расшифровано"));

        resultPanel.add(scrollEnc);
        resultPanel.add(scrollDec);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(resultPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadExample(ActionEvent e) {
        taOriginal.setText("Пример текста для шифрования. Сеть Фейштеля.");
        tfKey.setText("ключ_9");
        tfRounds.setText("24");
    }

    private void clearAll(ActionEvent e) {
        taOriginal.setText("");
        tfKey.setText("");
        tfRounds.setText("24");
        taEncryptedHex.setText("");
        taDecrypted.setText("");
    }

    private int getRounds() {
        try {
            int r = Integer.parseInt(tfRounds.getText().trim());
            if (r <= 0) return 24;
            return r;
        } catch (NumberFormatException ex) {
            return 24;
        }
    }

    private void encrypt(ActionEvent e) {
        try {
            String originalText = taOriginal.getText();
            if (originalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите исходный текст.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String keyStr = tfKey.getText();
            int rounds = getRounds();

            String encryptedHex = FeistelCipher.encryptText(originalText, keyStr, rounds);
            taEncryptedHex.setText(encryptedHex);
            taDecrypted.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decrypt(ActionEvent e) {
        try {
            String hex = taEncryptedHex.getText().replaceAll("\\s", "");
            if (hex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет зашифрованных данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String keyStr = tfKey.getText();
            int rounds = getRounds();

            String decrypted = FeistelCipher.decryptText(hex, keyStr, rounds);
            taDecrypted.setText(decrypted);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка дешифрования: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stepEncrypt(ActionEvent e) {
        try {
            String originalText = taOriginal.getText();
            if (originalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите исходный текст.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String keyStr = tfKey.getText();
            int rounds = getRounds();

            String log = FeistelCipher.encryptTextWithLog(originalText, keyStr, rounds);
            showLogDialog("Пошаговое шифрование", log);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stepDecrypt(ActionEvent e) {
        try {
            String hex = taEncryptedHex.getText().replaceAll("\\s", "");
            if (hex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет зашифрованных данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String keyStr = tfKey.getText();
            int rounds = getRounds();

            String log = FeistelCipher.decryptTextWithLog(hex, keyStr, rounds);
            showLogDialog("Пошаговое дешифрование", log);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка дешифрования: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLogDialog(String title, String log) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(log);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(ev -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}