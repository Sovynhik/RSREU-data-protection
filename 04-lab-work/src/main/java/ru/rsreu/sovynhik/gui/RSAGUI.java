package ru.rsreu.sovynhik.gui;

import ru.rsreu.sovynhik.core.RSAEngine;
import ru.rsreu.sovynhik.core.RSAEngineImpl;
import ru.rsreu.sovynhik.exception.RSAException;
import ru.rsreu.sovynhik.model.RSAKeySet;
import ru.rsreu.sovynhik.util.CipherFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.util.List;

public class RSAGUI extends JFrame {
    private final RSAEngine engine = new RSAEngineImpl();
    private RSAKeySet currentKeySet;

    private JTextField pField, qField;
    private JTextField dManualField, nManualField;
    private JTextArea messageArea;
    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JTextArea encryptedArea;
    private JTextArea decryptedArea;
    private JTextArea logArea;

    private JButton generateBtn, encryptBtn, decryptBtn, decryptManualBtn, clearLogBtn, clearAllBtn;

    public RSAGUI() {
        setTitle("RSA Encryption/Decryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 920);
        setMinimumSize(new Dimension(1050, 780));
        setLocationRelativeTo(null);
        initComponents();
        layoutComponents();
        setVisible(true);
    }

    private void initComponents() {
        pField = new JTextField(12);
        qField = new JTextField(12);
        dManualField = new JTextField(12);
        nManualField = new JTextField(12);

        messageArea = new JTextArea(6, 45);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        publicKeyArea = new JTextArea(4, 40);
        publicKeyArea.setEditable(false);
        publicKeyArea.setLineWrap(true);

        privateKeyArea = new JTextArea(4, 40);
        privateKeyArea.setEditable(false);
        privateKeyArea.setLineWrap(true);

        encryptedArea = new JTextArea(7, 45);
        encryptedArea.setLineWrap(true);
        encryptedArea.setWrapStyleWord(true);

        decryptedArea = new JTextArea(7, 45);
        decryptedArea.setEditable(false);
        decryptedArea.setLineWrap(true);
        decryptedArea.setWrapStyleWord(true);

        logArea = new JTextArea(12, 85);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        // Кнопки
        generateBtn = new JButton("Сгенерировать ключи из p и q");
        encryptBtn = new JButton("Зашифровать");
        decryptBtn = new JButton("Расшифровать (текущим ключом)");
        decryptManualBtn = new JButton("Дешифровать вручную");
        clearLogBtn = new JButton("Очистить лог");
        clearAllBtn = new JButton("Очистить всё");

        // Раскрашиваем кнопки
        generateBtn.setBackground(new Color(0, 123, 255));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);

        encryptBtn.setBackground(new Color(40, 167, 69));
        encryptBtn.setForeground(Color.WHITE);
        encryptBtn.setFocusPainted(false);

        decryptBtn.setBackground(new Color(23, 162, 184));
        decryptBtn.setForeground(Color.WHITE);
        decryptBtn.setFocusPainted(false);

        decryptManualBtn.setBackground(new Color(108, 117, 125));
        decryptManualBtn.setForeground(Color.WHITE);
        decryptManualBtn.setFocusPainted(false);

        clearLogBtn.setBackground(new Color(220, 53, 69));
        clearLogBtn.setForeground(Color.WHITE);
        clearLogBtn.setFocusPainted(false);

        clearAllBtn.setBackground(new Color(108, 117, 125));
        clearAllBtn.setForeground(Color.WHITE);
        clearAllBtn.setFocusPainted(false);

        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);
        generateBtn.setFont(btnFont);
        encryptBtn.setFont(btnFont);
        decryptBtn.setFont(btnFont);
        decryptManualBtn.setFont(btnFont);
        clearLogBtn.setFont(btnFont);
        clearAllBtn.setFont(btnFont);

        // Слушатели
        generateBtn.addActionListener(this::generateKeys);
        encryptBtn.addActionListener(this::encrypt);
        decryptBtn.addActionListener(this::decrypt);
        decryptManualBtn.addActionListener(this::decryptManual);
        clearLogBtn.addActionListener(e -> logArea.setText(""));
        clearAllBtn.addActionListener(e -> clearAllFields());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Верхняя панель (p и q)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        topPanel.add(new JLabel("p:"));
        topPanel.add(pField);
        topPanel.add(new JLabel("q:"));
        topPanel.add(qField);
        topPanel.add(generateBtn);

        // Панель ключей
        JPanel keyPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Сгенерированные ключи"));

        JPanel pubPanel = new JPanel(new BorderLayout(0, 5));
        pubPanel.add(new JLabel("Открытый ключ (e, n):"), BorderLayout.NORTH);
        pubPanel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);

        JPanel privPanel = new JPanel(new BorderLayout(0, 5));
        privPanel.add(new JLabel("Закрытый ключ (d, n, p, q):"), BorderLayout.NORTH);
        privPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);

        keyPanel.add(pubPanel);
        keyPanel.add(privPanel);

        // Панель ручного ввода ключа
        JPanel manualKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        manualKeyPanel.setBorder(BorderFactory.createTitledBorder("Ручной ввод ключа дешифрования"));
        manualKeyPanel.add(new JLabel("d:"));
        manualKeyPanel.add(dManualField);
        manualKeyPanel.add(new JLabel("n:"));
        manualKeyPanel.add(nManualField);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        leftPanel.add(keyPanel, BorderLayout.NORTH);
        leftPanel.add(manualKeyPanel, BorderLayout.SOUTH);

        // ====================== ЦЕНТРАЛЬНАЯ ЧАСТЬ ======================
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBorder(BorderFactory.createTitledBorder("Исходное сообщение"));
        msgPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // === Панель кнопок в ДВА РЯДА ===
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 12, 10));  // 2 ряда по 3 кнопки

        btnPanel.add(encryptBtn);
        btnPanel.add(decryptBtn);
        btnPanel.add(decryptManualBtn);
        btnPanel.add(clearLogBtn);
        btnPanel.add(clearAllBtn);

        // Добавляем пустую ячейку, чтобы кнопки выглядели симметрично
        btnPanel.add(new JLabel(""));

        JPanel topCenterPanel = new JPanel(new BorderLayout());
        topCenterPanel.add(msgPanel, BorderLayout.CENTER);
        topCenterPanel.add(btnPanel, BorderLayout.SOUTH);

        // Панели зашифрованного и расшифрованного текста
        JPanel encryptedPanel = new JPanel(new BorderLayout());
        encryptedPanel.setBorder(BorderFactory.createTitledBorder("Зашифрованный текст"));
        encryptedPanel.add(new JScrollPane(encryptedArea), BorderLayout.CENTER);

        JPanel decryptedPanel = new JPanel(new BorderLayout());
        decryptedPanel.setBorder(BorderFactory.createTitledBorder("Расшифрованный текст"));
        decryptedPanel.add(new JScrollPane(decryptedArea), BorderLayout.CENTER);

        JPanel cipherPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        cipherPanel.add(encryptedPanel);
        cipherPanel.add(decryptedPanel);

        centerPanel.add(topCenterPanel, BorderLayout.NORTH);
        centerPanel.add(cipherPanel, BorderLayout.CENTER);

        // Панель лога
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Лог операций"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Сборка окна
        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
    }

    // ==================== Методы обработки (без изменений) ====================

    private void generateKeys(ActionEvent event) {
        try {
            String pStr = pField.getText().trim();
            String qStr = qField.getText().trim();
            if (pStr.isEmpty() || qStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите p и q");
                return;
            }
            BigInteger p = new BigInteger(pStr);
            BigInteger q = new BigInteger(qStr);
            currentKeySet = engine.generateKeys(p, q);

            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            log("p = " + p + ", q = " + q);
            log("n = " + currentKeySet.getN());
            log("φ = " + phi);
            log("Выбрано минимальное e = " + currentKeySet.getE());
            log("d = " + currentKeySet.getD());
            log("Проверка: (e*d) mod φ = " + currentKeySet.getE().multiply(currentKeySet.getD()).mod(phi));

            publicKeyArea.setText("e = " + currentKeySet.getE() + "\nn = " + currentKeySet.getN());
            privateKeyArea.setText("d = " + currentKeySet.getD() + "\nn = " + currentKeySet.getN() +
                    "\np = " + p + ", q = " + q);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка ввода чисел: " + ex.getMessage());
        } catch (RSAException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void encrypt(ActionEvent ev) {
        if (currentKeySet == null) {
            JOptionPane.showMessageDialog(this, "Сначала сгенерируйте ключи из p и q");
            return;
        }
        String text = messageArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите сообщение для шифрования");
            return;
        }
        try {
            List<BigInteger> cipherList = engine.encrypt(text, currentKeySet.getE(), currentKeySet.getN());

            StringBuilder logMsg = new StringBuilder("Шифрование (открытый ключ e=" + currentKeySet.getE() +
                    ", n=" + currentKeySet.getN() + "):\n");
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                logMsg.append("  Символ '").append(ch).append("' (код ").append((int) ch).append(") -> ")
                        .append(cipherList.get(i)).append("\n");
            }

            encryptedArea.setText(CipherFormatter.formatCiphertext(cipherList));
            log(logMsg.toString());
        } catch (RSAException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка шифрования: " + ex.getMessage());
        }
    }

    private void decrypt(ActionEvent ev) {
        if (currentKeySet == null) {
            JOptionPane.showMessageDialog(this, "Сначала сгенерируйте ключи из p и q");
            return;
        }
        String cipherText = encryptedArea.getText().trim();
        if (cipherText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет зашифрованного текста");
            return;
        }
        try {
            List<BigInteger> cipherList = CipherFormatter.parseCiphertext(cipherText);
            String plain = engine.decrypt(cipherList, currentKeySet.getD(), currentKeySet.getN());

            StringBuilder logMsg = new StringBuilder("Дешифрование (закрытый ключ d=" + currentKeySet.getD() +
                    ", n=" + currentKeySet.getN() + "):\n");
            for (int i = 0; i < cipherList.size(); i++) {
                logMsg.append("  ").append(cipherList.get(i)).append(" -> символ '")
                        .append(plain.charAt(i)).append("' (код ").append((int) plain.charAt(i)).append(")\n");
            }
            decryptedArea.setText(plain);
            log(logMsg.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка формата зашифрованных чисел: " + ex.getMessage());
        } catch (RSAException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка дешифрования: " + ex.getMessage());
        }
    }

    private void decryptManual(ActionEvent ev) {
        String dStr = dManualField.getText().trim();
        String nStr = nManualField.getText().trim();
        if (dStr.isEmpty() || nStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите d и n для ручного дешифрования");
            return;
        }
        BigInteger dManual, nManual;
        try {
            dManual = new BigInteger(dStr);
            nManual = new BigInteger(nStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат числа в d или n");
            return;
        }

        String cipherText = encryptedArea.getText().trim();
        if (cipherText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет зашифрованного текста");
            return;
        }
        try {
            List<BigInteger> cipherList = CipherFormatter.parseCiphertext(cipherText);
            String plain = engine.decrypt(cipherList, dManual, nManual);

            StringBuilder logMsg = new StringBuilder("Ручное дешифрование (d=" + dManual + ", n=" + nManual + "):\n");
            for (int i = 0; i < cipherList.size(); i++) {
                logMsg.append("  ").append(cipherList.get(i)).append(" -> символ '")
                        .append(plain.charAt(i)).append("' (код ").append((int) plain.charAt(i)).append(")\n");
            }
            decryptedArea.setText(plain);
            log(logMsg.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка формата зашифрованных чисел: " + ex.getMessage());
        } catch (RSAException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка дешифрования: " + ex.getMessage());
        }
    }

    private void clearAllFields() {
        pField.setText("");
        qField.setText("");
        dManualField.setText("");
        nManualField.setText("");
        messageArea.setText("");
        publicKeyArea.setText("");
        privateKeyArea.setText("");
        encryptedArea.setText("");
        decryptedArea.setText("");
        logArea.setText("");
        currentKeySet = null;
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}