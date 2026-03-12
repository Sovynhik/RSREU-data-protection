package ru.rsreu.sovynhik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GammaCipherGUI extends JFrame {
    private Cipher cipher;  // больше не final, чтобы можно было заменить
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JTextField permutationField;
    private JButton setPermButton;

    public GammaCipherGUI(Cipher initialCipher) {
        this.cipher = initialCipher;

        setTitle("Перестановка (Вариант 9)");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Верхняя панель с вводом перестановки и кнопками шифрования
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // Панель ввода перестановки
        JPanel permPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        permPanel.add(new JLabel("Перестановка (числа через запятую или пробел):"));
        permutationField = new JTextField(20);
        permutationField.setText("2,5,3,4,1,6"); // пример
        permPanel.add(permutationField);
        setPermButton = new JButton("Установить");
        permPanel.add(setPermButton);
        topPanel.add(permPanel);

        // Панель с кнопками шифрования/дешифрования
        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Зашифровать");
        decryptButton = new JButton("Расшифровать");
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        topPanel.add(buttonPanel);

        add(topPanel, BorderLayout.NORTH);

        // Область ввода
        inputTextArea = new JTextArea();
        JScrollPane inputScroll = new JScrollPane(inputTextArea);
        add(inputScroll, BorderLayout.CENTER);

        // Область вывода
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputTextArea);
        add(outputScroll, BorderLayout.SOUTH);

        // Обработчики кнопок шифрования/дешифрования
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputTextArea.getText();
                String encrypted = cipher.encrypt(input);
                outputTextArea.setText(encrypted);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputTextArea.getText();
                try {
                    String decrypted = cipher.decrypt(input);
                    outputTextArea.setText(decrypted);
                } catch (Exception ex) {
                    outputTextArea.setText("Ошибка при расшифровке: " + ex.getMessage());
                }
            }
        });

        // Обработчик кнопки установки перестановки
        setPermButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = permutationField.getText().trim();
                try {
                    int[] perm = parsePermutation(text);
                    if (!isValidPermutation(perm)) {
                        JOptionPane.showMessageDialog(GammaCipherGUI.this,
                                "Некорректная перестановка.\nЧисла должны быть от 1 до " + perm.length + " и не повторяться.",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Создаём новый шифр с введённой перестановкой
                    cipher = new PermutationCipher(perm);
                    JOptionPane.showMessageDialog(GammaCipherGUI.this,
                            "Перестановка установлена.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GammaCipherGUI.this,
                            "Ошибка при разборе перестановки: " + ex.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Разбор строки с числами, разделёнными запятыми или пробелами
    private int[] parsePermutation(String text) throws NumberFormatException {
        String[] parts = text.split("[,\\s]+");
        int[] perm = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            perm[i] = Integer.parseInt(parts[i].trim());
        }
        return perm;
    }

    // Проверка, что массив содержит все числа от 1 до length без повторов
    private boolean isValidPermutation(int[] perm) {
        boolean[] seen = new boolean[perm.length];
        for (int num : perm) {
            if (num < 1 || num > perm.length) return false;
            if (seen[num - 1]) return false;
            seen[num - 1] = true;
        }
        return true;
    }

//    public static void main(String[] args) {
//        // Начальная перестановка по умолчанию
//        int[] defaultPerm = {2, 5, 3, 4, 1, 6};
//        Cipher cipher = new PermutationCipher(defaultPerm);
//
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new GammaCipherGUI(cipher).setVisible(true);
//            }
//        });
//    }
}