package ru.rsreu.sovynhik;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GammaCipherGUI {
    private JFrame frame;
    private JTextField aField, cField, t0Field, mField;
    private JTextArea plaintextArea, ciphertextArea, decryptedArea;
    private JTextArea plainIndicesArea, gammaArea, resultArea;
    private JLabel statusLabel;

    // Русский алфавит с буквой ё (33 буквы)
    private static final String ALPHABET_STRING = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private final Alphabet alphabet = new Alphabet(ALPHABET_STRING);

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
                UIManager.put("defaultFont", defaultFont);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GammaCipherGUI().createAndShowGUI();
        });
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public void createAndShowGUI() {
        frame = new JFrame("Лабораторная работа №2: Шифр гаммирования");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 900);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));

        // Заголовок
        JLabel titleLabel = new JLabel("Лабораторная работа №2: Шифр гаммирования");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("(русский алфавит с буквой ё)");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        contentPanel.add(createParamsPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        contentPanel.add(createAlphabetPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        contentPanel.add(createInputPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createButtonPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        contentPanel.add(createResultsPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        contentPanel.add(createDetailedPanel());

        scrollPane.setViewportView(contentPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createParamsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Параметры генератора ПСЧ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // A
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("A:"), gbc);
        gbc.gridx = 1;
        aField = new JTextField("5", 8);
        aField.setToolTipText("Множитель. Для максимального периода A mod 4 = 1");
        panel.add(aField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(A mod 4 = 1 для макс. периода)"), gbc);

        // C
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("C:"), gbc);
        gbc.gridx = 1;
        cField = new JTextField("3", 8);
        cField.setToolTipText("Приращение. Для максимального периода C должно быть нечётным");
        panel.add(cField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(C нечётное для макс. периода)"), gbc);

        // T0
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("T₀:"), gbc);
        gbc.gridx = 1;
        t0Field = new JTextField("7", 8);
        t0Field.setToolTipText("Начальное значение (зерно)");
        panel.add(t0Field, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(начальное значение)"), gbc);

        // M
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("M (2^b):"), gbc);
        gbc.gridx = 1;
        mField = new JTextField("32", 8);
        mField.setToolTipText("Модуль, обычно степень двойки");
        panel.add(mField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(по умолчанию 32 = 2⁵)"), gbc);

        // Кнопка проверки
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton checkButton = new JButton("Проверить параметры");
        checkButton.addActionListener(e -> checkParameters());
        panel.add(checkButton, gbc);

        return panel;
    }

    private JPanel createAlphabetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Используемый алфавит"));

        StringBuilder sb = new StringBuilder();
        sb.append("Русский алфавит с буквой ё (33 буквы):\n");
        for (int i = 0; i < alphabet.size(); i++) {
            sb.append(String.format("%2d:%s ", i + 1, alphabet.indexToChar(i + 1)));
            if ((i + 1) % 10 == 0) sb.append("\n");
        }

        JTextArea alphabetArea = new JTextArea(sb.toString());
        alphabetArea.setEditable(false);
        alphabetArea.setBackground(new Color(255, 255, 220));
        alphabetArea.setFont(new Font("Monospaced", Font.BOLD, 13));
        alphabetArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(new JScrollPane(alphabetArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ввод данных"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel label1 = new JLabel("Исходный текст (русские буквы):");
        label1.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label1, gbc);

        gbc.gridy = 1;
        gbc.weightx = 1; gbc.weighty = 0.3;
        plaintextArea = new JTextArea(3, 70);
        plaintextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        plaintextArea.setLineWrap(true);
        plaintextArea.setWrapStyleWord(true);
        plaintextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(plaintextArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        JLabel label2 = new JLabel("Зашифрованный текст (русские буквы):");
        label2.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label2, gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.3;
        ciphertextArea = new JTextArea(3, 70);
        ciphertextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        ciphertextArea.setLineWrap(true);
        ciphertextArea.setWrapStyleWord(true);
        ciphertextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(ciphertextArea), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton encryptButton = new JButton("🔒 Зашифровать");
        encryptButton.setFont(new Font("Arial", Font.BOLD, 12));
        encryptButton.addActionListener(e -> encryptText());
        panel.add(encryptButton);

        JButton decryptButton = new JButton("🔓 Расшифровать");
        decryptButton.setFont(new Font("Arial", Font.BOLD, 12));
        decryptButton.addActionListener(e -> decryptText());
        panel.add(decryptButton);

        JButton clearButton = new JButton("🗑 Очистить всё");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearButton.addActionListener(e -> clearAll());
        panel.add(clearButton);

        JButton exampleButton = new JButton("📋 Пример (абв)");
        exampleButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exampleButton.addActionListener(e -> loadExample());
        panel.add(exampleButton);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Результаты"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel label = new JLabel("Расшифрованный текст:");
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridy = 1;
        gbc.weightx = 1; gbc.weighty = 0.2;
        decryptedArea = new JTextArea(2, 70);
        decryptedArea.setFont(new Font("Arial", Font.PLAIN, 14));
        decryptedArea.setEditable(false);
        decryptedArea.setLineWrap(true);
        decryptedArea.setWrapStyleWord(true);
        decryptedArea.setBackground(new Color(240, 255, 240));
        decryptedArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(decryptedArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(statusLabel, gbc);

        return panel;
    }

    private JPanel createDetailedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Детальный вывод"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel label1 = new JLabel("Индексы исходных букв:");
        label1.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label1, gbc);

        gbc.gridy = 1;
        gbc.weightx = 1; gbc.weighty = 0.1;
        plainIndicesArea = new JTextArea(1, 80);
        plainIndicesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        plainIndicesArea.setEditable(false);
        plainIndicesArea.setBackground(Color.WHITE);
        plainIndicesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(plainIndicesArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        JLabel label2 = new JLabel("Гамма:");
        label2.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label2, gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gammaArea = new JTextArea(1, 80);
        gammaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gammaArea.setEditable(false);
        gammaArea.setBackground(Color.WHITE);
        gammaArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(gammaArea), gbc);

        gbc.gridy = 4;
        gbc.weighty = 0;
        JLabel label3 = new JLabel("Результат (подробно):");
        label3.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label3, gbc);

        gbc.gridy = 5;
        gbc.weighty = 0.5;
        resultArea = new JTextArea(5, 80);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(resultArea), gbc);

        return panel;
    }

    // --- Логика, использующая SOLID-классы ---

    private void encryptText() {
        try {
            int A = Integer.parseInt(aField.getText());
            int C = Integer.parseInt(cField.getText());
            int T0 = Integer.parseInt(t0Field.getText());
            int M = Integer.parseInt(mField.getText());

            String plaintext = plaintextArea.getText();
            if (plaintext.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите текст");
                return;
            }

            RandomGenerator generator = new LinearCongruentialGenerator(A, C, T0, M);
            CipherEngine engine = new CipherEngine(generator, alphabet);
            CipherResult result = engine.encrypt(plaintext);

            ciphertextArea.setText(result.text);
            plainIndicesArea.setText(formatList(result.indices) + " → " + result.text);
            gammaArea.setText(formatList(result.gamma));
            resultArea.setText(result.details);
            checkParameters();

            JOptionPane.showMessageDialog(frame,
                    String.format("Зашифровано %d букв(ы)", result.indices.size()),
                    "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка: введите числа в поля A, C, T0, M");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка: " + ex.getMessage());
        }
    }

    private void decryptText() {
        try {
            int A = Integer.parseInt(aField.getText());
            int C = Integer.parseInt(cField.getText());
            int T0 = Integer.parseInt(t0Field.getText());
            int M = Integer.parseInt(mField.getText());

            String ciphertext = ciphertextArea.getText();
            if (ciphertext.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите шифр");
                return;
            }

            RandomGenerator generator = new LinearCongruentialGenerator(A, C, T0, M);
            CipherEngine engine = new CipherEngine(generator, alphabet);
            CipherResult result = engine.decrypt(ciphertext);

            decryptedArea.setText(result.text);
            plainIndicesArea.setText(formatList(result.indices) + " → " + result.text);
            gammaArea.setText(formatList(result.gamma));
            resultArea.setText(result.details);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка: введите числа в поля A, C, T0, M");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка: " + ex.getMessage());
        }
    }

    private String formatList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int val : list) {
            sb.append(String.format("%2d ", val));
        }
        return sb.toString().trim();
    }

    private void checkParameters() {
        try {
            int A = Integer.parseInt(aField.getText());
            int C = Integer.parseInt(cField.getText());
            int M = Integer.parseInt(mField.getText());

            StringBuilder status = new StringBuilder();
            Color color = new Color(0, 128, 0);

            if (C % 2 == 1 && A % 4 == 1) {
                status.append("✓ Параметры A и C подходят для максимального периода");
            } else {
                status.append("⚠ Для максимального периода: C должно быть нечётным, A mod 4 = 1");
                color = new Color(255, 140, 0);
            }

            status.append(String.format(" (M=%d)", M));
            statusLabel.setText(status.toString());
            statusLabel.setForeground(color);

        } catch (NumberFormatException e) {
            statusLabel.setText("Введите числа в поля A, C, T0, M");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void clearAll() {
        plaintextArea.setText("");
        ciphertextArea.setText("");
        decryptedArea.setText("");
        plainIndicesArea.setText("");
        gammaArea.setText("");
        resultArea.setText("");
        statusLabel.setText("");
        aField.setText("5");
        cField.setText("3");
        t0Field.setText("7");
        mField.setText("32");
    }

    private void loadExample() {
        plaintextArea.setText("абв");
        aField.setText("5");
        cField.setText("3");
        t0Field.setText("7");
        mField.setText("32");

        String message = "Загружен пример 'абв'\n" +
                "Параметры: A=5, C=3, T0=7, M=32\n\n" +
                "Гамма: T1=6, T2=1, T3=8\n\n" +
                "Расчёт:\n" +
                "• а (1) ⊕ 6 = 7 → ё\n" +
                "• б (2) ⊕ 1 = 3 → в\n" +
                "• в (3) ⊕ 8 = 11 → й\n\n" +
                "РЕЗУЛЬТАТ: ёвй";

        JOptionPane.showMessageDialog(frame, message, "Пример из теории", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ---------- SOLID-классы (можно вынести в отдельные файлы) ----------

/**
 * Представляет алфавит и операции с ним.
 */
class Alphabet {
    private final String letters;

    public Alphabet(String letters) {
        this.letters = letters;
    }

    /**
     * Преобразует символ в индекс (начиная с 1). Если символ не найден, возвращает 0.
     */
    public int charToIndex(char c) {
        int idx = letters.indexOf(Character.toLowerCase(c));
        return idx >= 0 ? idx + 1 : 0;
    }

    /**
     * Преобразует индекс (1..size) в символ. При неверном индексе возвращает '?'.
     */
    public char indexToChar(int index) {
        if (index >= 1 && index <= letters.length()) {
            return letters.charAt(index - 1);
        }
        return '?';
    }

    public int size() {
        return letters.length();
    }
}

/**
 * Интерфейс генератора псевдослучайной последовательности.
 */
interface RandomGenerator {
    /**
     * Генерирует последовательность заданной длины.
     */
    List<Integer> generateSequence(int length);
}

/**
 * Линейный конгруэнтный генератор.
 */
class LinearCongruentialGenerator implements RandomGenerator {
    private final int a;
    private final int c;
    private final int t0;
    private final int m;

    public LinearCongruentialGenerator(int a, int c, int t0, int m) {
        this.a = a;
        this.c = c;
        this.t0 = t0;
        this.m = m;
    }

    @Override
    public List<Integer> generateSequence(int length) {
        List<Integer> sequence = new ArrayList<>();
        int t = t0;
        for (int i = 0; i < length; i++) {
            t = (a * t + c) % m;
            sequence.add(t);
        }
        return sequence;
    }
}

/**
 * Результат операции шифрования/дешифрования.
 */
class CipherResult {
    final String text;               // полученный текст
    final List<Integer> indices;      // индексы букв результата
    final List<Integer> gamma;        // использованная гамма
    final String details;             // детальный отчёт

    public CipherResult(String text, List<Integer> indices, List<Integer> gamma, String details) {
        this.text = text;
        this.indices = indices;
        this.gamma = gamma;
        this.details = details;
    }
}

/**
 * Движок шифрования и дешифрования методом гаммирования (XOR).
 */
class CipherEngine {
    private final RandomGenerator randomGenerator;
    private final Alphabet alphabet;

    public CipherEngine(RandomGenerator randomGenerator, Alphabet alphabet) {
        this.randomGenerator = randomGenerator;
        this.alphabet = alphabet;
    }

    /**
     * Шифрует открытый текст.
     */
    public CipherResult encrypt(String plaintext) {
        // Фильтрация и приведение к нижнему регистру
        StringBuilder filtered = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (alphabet.charToIndex(c) != 0) {
                filtered.append(Character.toLowerCase(c));
            }
        }
        String clean = filtered.toString();

        // Индексы открытого текста
        List<Integer> plainIndices = new ArrayList<>();
        for (char c : clean.toCharArray()) {
            plainIndices.add(alphabet.charToIndex(c));
        }

        // Гамма
        List<Integer> gamma = randomGenerator.generateSequence(plainIndices.size());

        // Шифрование XOR
        List<Integer> cipherIndices = new ArrayList<>();
        StringBuilder cipherText = new StringBuilder();
        StringBuilder details = new StringBuilder();

        details.append("╔════════════════════════════════════════════════════════════╗\n");
        details.append("║                    ШИФРОВАНИЕ                              ║\n");
        details.append("╚════════════════════════════════════════════════════════════╝\n\n");

        for (int i = 0; i < plainIndices.size(); i++) {
            int cIdx = plainIndices.get(i) ^ gamma.get(i);
            cipherIndices.add(cIdx);
            char cChar = alphabet.indexToChar(cIdx);
            cipherText.append(cChar);

            details.append(String.format("Шаг %d:\n", i + 1));
            details.append(String.format("  %s (индекс %2d) XOR %2d = %2d → %s\n",
                    clean.charAt(i), plainIndices.get(i), gamma.get(i), cIdx, cChar));
            details.append(String.format("  %2d (%s) ⊕ %2d = %2d (%s)\n\n",
                    plainIndices.get(i), clean.charAt(i), gamma.get(i), cIdx, cChar));
        }

        details.append("────────────────────────────────────────────────────\n");
        details.append(String.format("ИТОГО: %s → %s\n", clean, cipherText));
        details.append(String.format("Индексы: %s\n", formatList(plainIndices)));
        details.append(String.format("Гамма:   %s\n", formatList(gamma)));
        details.append(String.format("Результат XOR: %s\n", formatList(cipherIndices)));
        details.append(String.format("Буквы: %s\n", cipherText));

        return new CipherResult(cipherText.toString(), cipherIndices, gamma, details.toString());
    }

    /**
     * Дешифрует шифротекст.
     */
    public CipherResult decrypt(String ciphertext) {
        // Фильтрация
        StringBuilder filtered = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (alphabet.charToIndex(c) != 0) {
                filtered.append(Character.toLowerCase(c));
            }
        }
        String clean = filtered.toString();

        // Индексы шифротекста
        List<Integer> cipherIndices = new ArrayList<>();
        for (char c : clean.toCharArray()) {
            cipherIndices.add(alphabet.charToIndex(c));
        }

        // Гамма (та же, что при шифровании)
        List<Integer> gamma = randomGenerator.generateSequence(cipherIndices.size());

        // Дешифрование XOR
        List<Integer> plainIndices = new ArrayList<>();
        StringBuilder plainText = new StringBuilder();
        StringBuilder details = new StringBuilder();

        details.append("╔════════════════════════════════════════════════════════════╗\n");
        details.append("║                    ДЕШИФРОВАНИЕ                            ║\n");
        details.append("╚════════════════════════════════════════════════════════════╝\n\n");

        for (int i = 0; i < cipherIndices.size(); i++) {
            int pIdx = cipherIndices.get(i) ^ gamma.get(i);
            plainIndices.add(pIdx);
            char pChar = alphabet.indexToChar(pIdx);
            plainText.append(pChar);

            details.append(String.format("Шаг %d:\n", i + 1));
            details.append(String.format("  %s (индекс %2d) XOR %2d = %2d → %s\n",
                    clean.charAt(i), cipherIndices.get(i), gamma.get(i), pIdx, pChar));
            details.append(String.format("  %2d (%s) ⊕ %2d = %2d (%s)\n\n",
                    cipherIndices.get(i), clean.charAt(i), gamma.get(i), pIdx, pChar));
        }

        details.append("────────────────────────────────────────────────────\n");
        details.append(String.format("ИТОГО: %s → %s\n", clean, plainText));
        details.append(String.format("Индексы шифра: %s\n", formatList(cipherIndices)));
        details.append(String.format("Гамма:         %s\n", formatList(gamma)));
        details.append(String.format("Результат XOR: %s\n", formatList(plainIndices)));
        details.append(String.format("Расшифровано: %s\n", plainText));

        return new CipherResult(plainText.toString(), plainIndices, gamma, details.toString());
    }

    private String formatList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int v : list) {
            sb.append(String.format("%2d ", v));
        }
        return sb.toString().trim();
    }
}