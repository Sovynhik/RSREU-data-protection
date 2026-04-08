package ru.rsreu.sovynhik;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class DigitalSignature extends JFrame {
    private JTextArea messageArea;
    private JTextArea signatureArea;
    private JButton signButton, verifyButton, loadFromFile, generateButton, clearButton;

    private JTextField pField, qField, aField, yField, r1Field, sField;

    private BigInteger p, q, a, x, y;

    public DigitalSignature() {
        setTitle("ЭЦП (Вариант 9) — Подсчёт единиц в бинарном представлении");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 820);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // === Панель параметров ===
        JPanel paramsPanel = new JPanel(new GridBagLayout());
        paramsPanel.setBorder(BorderFactory.createTitledBorder("Параметры ЭЦП"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addLabelAndField(paramsPanel, gbc, 0, "p (простое число):", pField = new JTextField("7", 20));
        addLabelAndField(paramsPanel, gbc, 1, "q (простое число):", qField = new JTextField("3", 20));
        addLabelAndField(paramsPanel, gbc, 2, "a (основание 1 < a < p-1):", aField = new JTextField("2", 20));
        addLabelAndField(paramsPanel, gbc, 3, "Открытый ключ y:", yField = new JTextField("", 20));
        addLabelAndField(paramsPanel, gbc, 4, "r1 (компонента подписи):", r1Field = new JTextField("", 20));
        addLabelAndField(paramsPanel, gbc, 5, "s (компонента подписи):", sField = new JTextField("", 20));

        add(paramsPanel, BorderLayout.NORTH);

        // === Центральная панель ===
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Сообщение
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
        messageArea = new JTextArea(7, 65);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        generateButton = new JButton("Сгенерировать ключи");
        signButton = new JButton("Подписать сообщение");
        verifyButton = new JButton("Проверить подпись");
        loadFromFile = new JButton("Загрузить из файла");
        clearButton = new JButton("Очистить всё");

        // Цвета кнопок (работают хорошо с FlatLaf и обычным L&F)
        signButton.setBackground(new Color(40, 167, 69));
        signButton.setForeground(Color.WHITE);
        verifyButton.setBackground(new Color(0, 123, 255));
        verifyButton.setForeground(Color.WHITE);

        buttonPanel.add(generateButton);
        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);
        buttonPanel.add(loadFromFile);
        buttonPanel.add(clearButton);

        // Результаты
        JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.setBorder(BorderFactory.createTitledBorder("Протокол выполнения"));
        signatureArea = new JTextArea(24, 70);
        signatureArea.setLineWrap(true);
        signatureArea.setWrapStyleWord(true);
        signatureArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        signatureArea.setEditable(false);
        signaturePanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);

        mainPanel.add(messagePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(signaturePanel);

        add(mainPanel, BorderLayout.CENTER);

        // Слушатели
        generateButton.addActionListener(e -> generateKeys());
        signButton.addActionListener(e -> signMessage());
        verifyButton.addActionListener(e -> verifySignature());
        loadFromFile.addActionListener(e -> loadFile());
        clearButton.addActionListener(e -> clearAll());
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private boolean validateParameters(boolean forVerify) {
        try {
            p = new BigInteger(pField.getText().trim());
            q = new BigInteger(qField.getText().trim());
            a = new BigInteger(aField.getText().trim());

            if (p.compareTo(BigInteger.ZERO) <= 0 || !p.isProbablePrime(100)) {
                showError("p должно быть простым числом > 0");
                return false;
            }
            if (q.compareTo(BigInteger.ZERO) <= 0 || !q.isProbablePrime(100)) {
                showError("q должно быть простым числом > 0");
                return false;
            }
            if (a.compareTo(BigInteger.ONE) <= 0 || a.compareTo(p.subtract(BigInteger.ONE)) >= 0) {
                showError("a должно удовлетворять условию 1 < a < p-1");
                return false;
            }
            if (!a.pow(q.intValueExact()).mod(p).equals(BigInteger.ONE)) {
                showError("Условие a^q ≡ 1 (mod p) не выполнено");
                return false;
            }

            if (forVerify) {
                if (yField.getText().trim().isEmpty()) {
                    showError("Введите открытый ключ y");
                    return false;
                }
                y = new BigInteger(yField.getText().trim());
                if (y.compareTo(BigInteger.ZERO) <= 0 || y.compareTo(p) >= 0) {
                    showError("y должно быть в диапазоне 0 < y < p");
                    return false;
                }

                BigInteger r1 = new BigInteger(r1Field.getText().trim());
                BigInteger s = new BigInteger(sField.getText().trim());
                if (r1.compareTo(BigInteger.ZERO) <= 0 || r1.compareTo(q) >= 0) {
                    showError("r1 должно быть в диапазоне 0 < r1 < q");
                    return false;
                }
                if (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
                    showError("s должно быть в диапазоне 0 < s < q");
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            showError("Ошибка ввода параметров: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void generateKeys() {
        if (!validateParameters(false)) return;

        initSignatureParameters();
        JOptionPane.showMessageDialog(this,
                "Ключи успешно сгенерированы!\nОткрытый ключ y = " + y,
                "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initSignatureParameters() {
        Random rand = new Random();
        x = new BigInteger(q.bitLength() - 1, rand)
                .mod(q.subtract(BigInteger.ONE))
                .add(BigInteger.ONE);
        y = a.modPow(x, p);

        yField.setText(y.toString());

        signatureArea.setText("Ключи сгенерированы:\n" +
                "Приватный ключ x = " + x + "\n" +
                "Открытый ключ   y = " + y + "\n\n");
    }

    private BigInteger hashFunction(String message) {
        int ones = 0;
        signatureArea.append("=== ХЭШ-ФУНКЦИЯ (вариант 9) ===\n");

        for (char c : message.toCharArray()) {
            String bin = Integer.toBinaryString(c);
            signatureArea.append(bin + " ");
            for (char bit : bin.toCharArray()) {
                if (bit == '1') ones++;
            }
        }
        signatureArea.append("\nКоличество единиц = " + ones + "\n");

        BigInteger h = BigInteger.valueOf(ones);
        if (h.mod(q).equals(BigInteger.ZERO)) h = BigInteger.ONE;

        signatureArea.append("Хэш h(m) = " + h + "\n\n");
        return h;
    }

    private void signMessage() {
        if (!validateParameters(false)) return;

        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) {
            showError("Введите сообщение для подписи!");
            return;
        }

        initSignatureParameters();
        BigInteger h = hashFunction(msg);

        Random rand = new Random();
        BigInteger r1, s, k;
        int attempts = 0;

        do {
            k = new BigInteger(q.bitLength() - 1, rand)
                    .mod(q.subtract(BigInteger.ONE))
                    .add(BigInteger.ONE);
            BigInteger r = a.modPow(k, p);
            r1 = r.mod(q);
            s = x.multiply(r1).add(k.multiply(h)).mod(q);
            attempts++;
        } while ((r1.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO)) && attempts < 20000);

        if (attempts >= 20000) {
            showError("Не удалось сгенерировать подпись");
            return;
        }

        signatureArea.append("=== ПОДПИСЬ УСПЕШНО СОЗДАНА ===\n");
        signatureArea.append("r1 = " + r1 + "\n");
        signatureArea.append("s  = " + s + "\n\n");

        r1Field.setText(r1.toString());
        sField.setText(s.toString());
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                messageArea.setText(sb.toString().trim());
            } catch (IOException ex) {
                showError("Не удалось прочитать файл");
            }
        }
    }

    private void verifySignature() {
        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) {
            showError("Введите сообщение для проверки!");
            return;
        }
        if (!validateParameters(true)) return;

        try {
            BigInteger r1 = new BigInteger(r1Field.getText().trim());
            BigInteger s = new BigInteger(sField.getText().trim());
            BigInteger h = hashFunction(msg);

            BigInteger v = h.modPow(q.subtract(BigInteger.TWO), q);
            BigInteger z1 = s.multiply(v).mod(q);
            BigInteger z2 = q.subtract(r1).multiply(v).mod(q);
            BigInteger u = a.modPow(z1, p)
                    .multiply(y.modPow(z2, p))
                    .mod(p)
                    .mod(q);

            StringBuilder rep = new StringBuilder("=== ПРОТОКОЛ ПРОВЕРКИ ===\n\n");
            rep.append("p = ").append(p).append("\nq = ").append(q).append("\na = ").append(a)
                    .append("\ny = ").append(y).append("\nr1 = ").append(r1).append("\ns = ").append(s)
                    .append("\nh = ").append(h).append("\n\n");
            rep.append("v  = ").append(v).append("\nz1 = ").append(z1).append("\nz2 = ").append(z2)
                    .append("\nu  = ").append(u).append("\n\n");

            if (u.equals(r1)) {
                rep.append("ПОДПИСЬ ДЕЙСТВИТЕЛЬНА ✓");
                signatureArea.setBackground(new Color(220, 255, 220));
            } else {
                rep.append("ПОДПИСЬ НЕДЕЙСТВИТЕЛЬНА ✗");
                signatureArea.setBackground(new Color(255, 220, 220));
            }

            signatureArea.append(rep.toString());

        } catch (Exception ex) {
            showError("Ошибка при проверке: " + ex.getMessage());
        }
    }

    private void clearAll() {
        messageArea.setText("");
        signatureArea.setText("");
        r1Field.setText("");
        sField.setText("");
        signatureArea.setBackground(Color.WHITE);
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("FlatLaf не доступен, используем системный стиль");
        }

        SwingUtilities.invokeLater(() -> new DigitalSignature().setVisible(true));
    }
}