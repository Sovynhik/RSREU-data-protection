package ru.rsreu.sovynhik.pract_04.ui;

import ru.rsreu.sovynhik.pract_04.model.PasswordCalculator;
import ru.rsreu.sovynhik.pract_04.model.PasswordGenerator;
import ru.rsreu.sovynhik.pract_04.utils.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextField fieldP;
    private JTextField fieldV;
    private JTextField fieldT;
    private JTextField fieldUserCount;
    private JTextField fieldSStar;
    private JTextField fieldMinLength;
    private JTable tablePasswords;
    private DefaultTableModel tableModel;

    public MainFrame() {
        initUI();
        setDefaultValues();
    }

    private void initUI() {
        setTitle("Генератор паролей (вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createInputPanel(), BorderLayout.WEST);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createOutputPanel(), BorderLayout.EAST);
        add(createInfoPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Входные данные"));
        panel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        String[] labels = {"P (вероятность):", "V (паролей/мин):", "T (дней):", "Пользователей:"};
        JTextField[] fields = {fieldP = new JTextField(), fieldV = new JTextField(),
                fieldT = new JTextField(), fieldUserCount = new JTextField()};

        fieldP.setToolTipText("Например: 0.0001, 1e-4, 10^-4");
        fieldV.setToolTipText("Скорость перебора (паролей в минуту)");
        fieldT.setToolTipText("Срок действия пароля в днях");
        fieldUserCount.setToolTipText("Количество генерируемых паролей");

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i * 2;
            gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 0;
            gbc.gridy = i * 2 + 1;
            gbc.weightx = 1.0;
            panel.add(fields[i], gbc);
        }
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Кнопка генерации паролей
        JButton btnGenerate = new JButton("Генератор паролей");
        btnGenerate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnGenerate.setBackground(new Color(0, 120, 215));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setPreferredSize(new Dimension(220, 50));
        btnGenerate.addActionListener(e -> generate());
        panel.add(btnGenerate, gbc);

        // Кнопка метода "запрос-ответ"
        gbc.gridy = 1;
        JButton btnAuth = new JButton("Метод запрос-ответ");
        btnAuth.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAuth.setBackground(new Color(0, 150, 100));
        btnAuth.setForeground(Color.WHITE);
        btnAuth.setFocusPainted(false);
        btnAuth.setPreferredSize(new Dimension(220, 50));
        btnAuth.addActionListener(e -> {
            AuthDialog dialog = new AuthDialog(this);
            dialog.setVisible(true);
        });
        panel.add(btnAuth, gbc);

        // Кнопка регистрации
        gbc.gridy = 2;
        JButton btnRegister = new JButton("Регистрация");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBackground(new Color(0, 100, 150));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(220, 50));
        btnRegister.addActionListener(e -> openRegisterDialog());
        panel.add(btnRegister, gbc);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Результаты"));
        panel.setPreferredSize(new Dimension(400, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Нижняя граница S*:"), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        fieldSStar = new JTextField();
        fieldSStar.setEditable(false);
        fieldSStar.setBackground(Color.WHITE);
        panel.add(fieldSStar, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Минимальная длина L:"), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        fieldMinLength = new JTextField();
        fieldMinLength.setEditable(false);
        fieldMinLength.setBackground(Color.WHITE);
        panel.add(fieldMinLength, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Сгенерированные пароли:"), gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        tableModel = new DefaultTableModel(new String[]{"№", "Пароль"}, 0);
        tablePasswords = new JTable(tableModel);
        tablePasswords.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tablePasswords.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablePasswords.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablePasswords.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(tablePasswords);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        JLabel info = new JLabel("Алфавит: 69 символов (a-z, A-Z, 0-9, !\"#$%&')");
        info.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(info);
        return panel;
    }

    private void setDefaultValues() {
        fieldP.setText("1e-4");
        fieldV.setText("3");          // для 9-го варианта
        fieldT.setText("15");
        fieldUserCount.setText("10");
    }

    private void generate() {
        InputValidator.ValidationResult result = InputValidator.validate(
                fieldP.getText(), fieldV.getText(), fieldT.getText(), fieldUserCount.getText());

        if (!result.success) {
            JOptionPane.showMessageDialog(this, result.errorMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PasswordCalculator calculator = new PasswordCalculator(result.P, result.V, result.T, PasswordGenerator.ALPHABET_SIZE);
        fieldSStar.setText(formatNumber(calculator.getS_star()));
        fieldMinLength.setText(String.valueOf(calculator.getMinLength()));

        PasswordGenerator generator = new PasswordGenerator();
        List<String> passwords = generator.generatePasswords(result.userCount, calculator.getMinLength());

        tableModel.setRowCount(0);
        for (int i = 0; i < passwords.size(); i++) {
            tableModel.addRow(new Object[]{i + 1, passwords.get(i)});
        }
    }

    private void openRegisterDialog() {
        InputValidator.ValidationResult result = InputValidator.validate(
                fieldP.getText(), fieldV.getText(), fieldT.getText(), "1");
        if (!result.success) {
            JOptionPane.showMessageDialog(this,
                    "Для регистрации необходимо корректно заполнить поля P, V, T.\n" + result.errorMessage,
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RegisterDialog dialog = new RegisterDialog(this, result.P, result.V, result.T);
        dialog.setVisible(true);
    }

    private String formatNumber(long number) {
        if (number < 1_000) return String.valueOf(number);
        if (number < 1_000_000) return String.format("%,d", number);
        if (number < 1_000_000_000) return String.format("%,d (%.2f млн)", number, number / 1_000_000.0);
        return String.format("%,d (%.2f млрд)", number, number / 1_000_000_000.0);
    }
}