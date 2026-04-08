package ru.rsreu.sovynhik.ui;

import ru.rsreu.sovynhik.cipher.Cipher;
import ru.rsreu.sovynhik.cipher.PermutationCipher;
import ru.rsreu.sovynhik.util.PermutationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PermutationCipherGUI extends JFrame {
    private Cipher cipher;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField permutationField;
    private JButton setPermButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;

    public PermutationCipherGUI(Cipher initialCipher) {
        this.cipher = initialCipher;

        setTitle("Шифр перестановки (Вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Используем GridBagLayout для главной панели
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Строка 0: метка "Перестановка" + поле ввода + кнопка "Установить" ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Перестановка:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        permutationField = new JTextField(20);
        permutationField.setText("2,5,3,4,1,6"); // значение по умолчанию
        mainPanel.add(permutationField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        setPermButton = createStyledButton("Установить", new Color(180, 180, 180));
        mainPanel.add(setPermButton, gbc);

        // === Строка 1: кнопки "Зашифровать", "Расшифровать", "Очистить" ===
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        encryptButton = createStyledButton("Зашифровать", new Color(76, 175, 80));
        decryptButton = createStyledButton("Расшифровать", new Color(255, 152, 0));
        clearButton = createStyledButton("Очистить", new Color(244, 67, 54));
        actionPanel.add(encryptButton);
        actionPanel.add(decryptButton);
        actionPanel.add(clearButton);
        mainPanel.add(actionPanel, gbc);

        // === Строка 2: метка "Исходный текст" ===
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Исходный текст:"), gbc);

        // === Строка 3: область ввода текста ===
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputTextArea);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(inputScroll, gbc);

        // === Строка 4: метка "Результат" ===
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Результат:"), gbc);

        // === Строка 5: область вывода результата ===
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane outputScroll = new JScrollPane(outputTextArea);
        outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(outputScroll, gbc);

        add(mainPanel);

        // === Привязка обработчиков ===
        setPermButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyPermutation();
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptText();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptText();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }

    /** Создаёт стилизованную кнопку с заданным цветом фона */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setFocusPainted(false);
        return button;
    }

    /** Применить новую перестановку из поля ввода */
    private void applyPermutation() {
        String text = permutationField.getText().trim();
        try {
            int[] perm = PermutationUtils.parsePermutation(text);
            if (!PermutationUtils.isValidPermutation(perm)) {
                JOptionPane.showMessageDialog(this,
                        "Некорректная перестановка.\nЧисла должны быть от 1 до " + perm.length + " и не повторяться.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            cipher = new PermutationCipher(perm);
            JOptionPane.showMessageDialog(this,
                    "Перестановка установлена.", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при разборе перестановки: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Зашифровать текст из верхнего поля */
    private void encryptText() {
        String input = inputTextArea.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст для шифрования.");
            return;
        }
        try {
            String encrypted = cipher.encrypt(input);
            outputTextArea.setText(encrypted);
        } catch (Exception ex) {
            outputTextArea.setText("Ошибка шифрования: " + ex.getMessage());
        }
    }

    /** Расшифровать текст из верхнего поля */
    private void decryptText() {
        String input = inputTextArea.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст для расшифровки.");
            return;
        }
        try {
            String decrypted = cipher.decrypt(input);
            outputTextArea.setText(decrypted);
        } catch (Exception ex) {
            outputTextArea.setText("Ошибка расшифровки: " + ex.getMessage());
        }
    }

    /** Очистить все поля */
    private void clearAll() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        permutationField.setText("2,5,3,4,1,6"); // сброс к начальному значению (можно оставить текущее, но лучше так)
        // При желании можно также сбросить cipher к начальной перестановке, но это необязательно
    }
}