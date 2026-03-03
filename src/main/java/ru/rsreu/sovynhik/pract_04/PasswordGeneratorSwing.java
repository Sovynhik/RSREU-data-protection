package ru.rsreu.sovynhik.pract_04;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class PasswordGeneratorSwing extends JFrame {
    private JTextField fieldP;
    private JTextField fieldV;
    private JTextField fieldT;
    private JTextField fieldUserCount;
    private JTextField fieldSStar;
    private JTextField fieldMinLength;
    private JTextArea textAreaPasswords;

    // Алфавит: латинские буквы (разные регистры), цифры, спецсимволы
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789" +
            "!\"#$%&'"; // 69 символов

    public PasswordGeneratorSwing() {
        setTitle("Генератор паролей (вариант 9)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Левая колонка: поля ввода
        JPanel leftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        leftPanel.add(new JLabel("P — вероятность подбора:"));
        fieldP = new JTextField("1e-4");
        leftPanel.add(fieldP);
        leftPanel.add(new JLabel("V — скорость перебора (паролей/мин):"));
        fieldV = new JTextField("3");
        leftPanel.add(fieldV);
        leftPanel.add(new JLabel("T — срок действия (дней):"));
        fieldT = new JTextField("15");
        leftPanel.add(fieldT);
        leftPanel.add(new JLabel("Количество пользователей:"));
        fieldUserCount = new JTextField("10");
        leftPanel.add(fieldUserCount);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, gbc);

        // Центр: кнопка
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JButton btnGenerate = new JButton("Сгенерировать");
        btnGenerate.setPreferredSize(new Dimension(180, 40));
        centerPanel.add(btnGenerate);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        add(centerPanel, gbc);

        // Правая колонка: поля вывода
        JPanel rightPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        rightPanel.add(new JLabel("Нижняя граница S*:"));
        fieldSStar = new JTextField();
        fieldSStar.setEditable(false);
        rightPanel.add(fieldSStar);
        rightPanel.add(new JLabel("Минимальная длина пароля L:"));
        fieldMinLength = new JTextField();
        fieldMinLength.setEditable(false);
        rightPanel.add(fieldMinLength);
        rightPanel.add(new JLabel("Список паролей:"));
        textAreaPasswords = new JTextArea(8, 20);
        textAreaPasswords.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaPasswords);
        rightPanel.add(scrollPane);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        // Обработчик кнопки
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePasswords();
            }
        });
    }

    private void generatePasswords() {
        try {
            double P = Double.parseDouble(fieldP.getText().trim());
            double V_per_min = Double.parseDouble(fieldV.getText().trim());
            int T_days = Integer.parseInt(fieldT.getText().trim());
            int userCount = Integer.parseInt(fieldUserCount.getText().trim());

            long T_minutes = T_days * 24L * 60L;
            double totalAttempts = V_per_min * T_minutes;
            long S_star = (long) Math.ceil(totalAttempts / P);
            fieldSStar.setText(String.valueOf(S_star));

            int A = ALPHABET.length();
            int L = (int) Math.ceil(Math.log(S_star) / Math.log(A));
            fieldMinLength.setText(String.valueOf(L));

            Random rand = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userCount; i++) {
                StringBuilder password = new StringBuilder();
                for (int j = 0; j < L; j++) {
                    password.append(ALPHABET.charAt(rand.nextInt(A)));
                }
                sb.append("User").append(i + 1).append(": ").append(password).append("\n");
            }
            textAreaPasswords.setText(sb.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка в формате чисел. Проверьте введённые данные.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Произошла ошибка: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PasswordGeneratorSwing().setVisible(true);
            }
        });
    }
}
