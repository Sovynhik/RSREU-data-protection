package ru.rsreu.sovynhik.ui;

import ru.rsreu.sovynhik.cipher.GammaCipher;
import ru.rsreu.sovynhik.generator.ByteGenerator;
import ru.rsreu.sovynhik.generator.LinearCongruentialGenerator;
import ru.rsreu.sovynhik.model.GeneratorParameters;
import ru.rsreu.sovynhik.util.HexUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class MainFrame extends JFrame {
    private static final long MODULUS = 256L; // 2^8

    private final JTextArea taOriginal = new JTextArea(5, 40);
    private final VariantPanel variant1 = new VariantPanel("Вариант 1");
    private final VariantPanel variant2 = new VariantPanel("Вариант 2");

    public MainFrame() {
        setTitle("Лабораторная работа №2 - Гаммирование (Вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Панель исходного текста
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Исходный текст"));
        taOriginal.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taOriginal.setLineWrap(true);
        taOriginal.setWrapStyleWord(true);
        topPanel.add(new JScrollPane(taOriginal), BorderLayout.CENTER);

        // Кнопки управления
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton btnLoadExample = new JButton("Загрузить пример");
        styleButton(btnLoadExample, new Color(76, 175, 80), Color.WHITE); // зелёный
        btnLoadExample.addActionListener(e -> loadExampleText());
        controlPanel.add(btnLoadExample);

        JButton btnCompare = new JButton("Сравнить шифрограммы");
        styleButton(btnCompare, new Color(255, 152, 0), Color.WHITE); // оранжевый
        btnCompare.addActionListener(e -> compareCiphergrams());
        controlPanel.add(btnCompare);

        JButton btnClear = new JButton("Очистить всё");
        styleButton(btnClear, new Color(244, 67, 54), Color.WHITE); // красный
        btnClear.addActionListener(e -> clearAll());
        controlPanel.add(btnClear);

        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // Панели вариантов
        JPanel variantsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        variantsPanel.add(variant1);
        variantsPanel.add(variant2);

        add(topPanel, BorderLayout.NORTH);
        add(variantsPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    /** Единый метод для стилизации кнопок (FlatLaf) */
    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        // Скруглённые углы (работает только в FlatLaf)
        button.putClientProperty("JButton.arc", 12);
        // FlatLaf автоматически обработает hover и pressed
        button.setFocusPainted(false); // убираем контур фокуса для чистоты
    }

    private void loadExampleText() {
        taOriginal.setText("Hello, world! Привет, мир! 123");
    }

    private void clearAll() {
        taOriginal.setText("");
        variant1.clear();
        variant2.clear();
    }

    private void compareCiphergrams() {
        byte[] bytes1 = variant1.getLastEncryptedBytes();
        byte[] bytes2 = variant2.getLastEncryptedBytes();

        if (bytes1 == null || bytes2 == null) {
            JOptionPane.showMessageDialog(this,
                    "Сначала зашифруйте текст обоими вариантами.",
                    "Сравнение невозможно",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean equal = java.util.Arrays.equals(bytes1, bytes2);
        String message = equal
                ? "Шифрограммы полностью совпадают."
                : "Шифрограммы различаются.";
        JOptionPane.showMessageDialog(this, message, "Результат сравнения",
                equal ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.PLAIN_MESSAGE);
    }

    // Внутренний класс панели для одного варианта параметров
    private class VariantPanel extends JPanel {
        private final String title;
        private final JTextField tfA = new JTextField("5", 5);
        private final JTextField tfC = new JTextField("3", 5);
        private final JTextField tfSeed = new JTextField("7", 5);
        private final JTextArea taEncryptedHex = new JTextArea(3, 20);
        private final JTextArea taDecrypted = new JTextArea(3, 20);
        private final JButton btnEncrypt = new JButton("Шифровать");
        private final JButton btnDecrypt = new JButton("Дешифровать");

        private byte[] lastEncryptedBytes; // для дешифрования и сравнения

        public VariantPanel(String title) {
            this.title = title;
            setBorder(BorderFactory.createTitledBorder(title));
            setLayout(new BorderLayout(5, 5));

            // Стилизация кнопок варианта
            styleButton(btnEncrypt, new Color(33, 150, 243), Color.WHITE); // синий
            styleButton(btnDecrypt, new Color(76, 175, 80), Color.WHITE);  // зелёный

            // Панель ввода параметров
            JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            paramsPanel.add(new JLabel("A:"));
            paramsPanel.add(tfA);
            paramsPanel.add(new JLabel("C:"));
            paramsPanel.add(tfC);
            paramsPanel.add(new JLabel("Seed:"));
            paramsPanel.add(tfSeed);
            paramsPanel.add(btnEncrypt);
            paramsPanel.add(btnDecrypt);

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

            add(paramsPanel, BorderLayout.NORTH);
            add(resultPanel, BorderLayout.CENTER);

            // Обработчики
            btnEncrypt.addActionListener(e -> encrypt());
            btnDecrypt.addActionListener(e -> decrypt());
        }

        private long getA() {
            return Long.parseLong(tfA.getText().trim());
        }

        private long getC() {
            return Long.parseLong(tfC.getText().trim());
        }

        private long getSeed() {
            return Long.parseLong(tfSeed.getText().trim());
        }

        private void encrypt() {
            try {
                String originalText = taOriginal.getText();
                if (originalText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Введите исходный текст.",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);
                GeneratorParameters params = new GeneratorParameters(getA(), getC(), getSeed(), MODULUS);
                ByteGenerator generator = new LinearCongruentialGenerator(params);
                GammaCipher cipher = new GammaCipher();

                lastEncryptedBytes = cipher.encrypt(originalBytes, generator);
                String hex = HexUtils.bytesToHex(lastEncryptedBytes);
                taEncryptedHex.setText(hex);

                // Очистить предыдущий результат расшифровки
                taDecrypted.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Параметры A, C, Seed должны быть целыми числами.",
                        "Ошибка ввода",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void decrypt() {
            if (lastEncryptedBytes == null) {
                JOptionPane.showMessageDialog(this,
                        "Сначала зашифруйте текст.",
                        "Нет данных",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                GeneratorParameters params = new GeneratorParameters(getA(), getC(), getSeed(), MODULUS);
                ByteGenerator generator = new LinearCongruentialGenerator(params);
                GammaCipher cipher = new GammaCipher();

                byte[] decryptedBytes = cipher.decrypt(lastEncryptedBytes, generator);
                String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
                taDecrypted.setText(decryptedText);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Параметры A, C, Seed должны быть целыми числами.",
                        "Ошибка ввода",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        public byte[] getLastEncryptedBytes() {
            return lastEncryptedBytes;
        }

        public void clear() {
            tfA.setText("5");
            tfC.setText("3");
            tfSeed.setText("7");
            taEncryptedHex.setText("");
            taDecrypted.setText("");
            lastEncryptedBytes = null;
        }
    }
}