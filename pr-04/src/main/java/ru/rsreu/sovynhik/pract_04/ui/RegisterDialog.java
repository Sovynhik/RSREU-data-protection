package ru.rsreu.sovynhik.pract_04.ui;

import ru.rsreu.sovynhik.pract_04.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegisterDialog extends JDialog {
    private final JTextField loginField;
    private final List<String> selectedQuestions;
    private final List<JTextField> answerFields;
    private final PasswordGenerator passwordGenerator;
    private final int passwordLength;

    public RegisterDialog(JFrame parent, double P, double V, int T) {
        super(parent, "Регистрация нового пользователя", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        PasswordCalculator calculator = new PasswordCalculator(P, V, T, PasswordGenerator.ALPHABET_SIZE);
        this.passwordLength = calculator.getMinLength();
        this.passwordGenerator = new PasswordGenerator();

        // Верхняя панель с логином
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Логин:"));
        loginField = new JTextField(15);
        topPanel.add(loginField);
        add(topPanel, BorderLayout.NORTH);

        // Панель с вопросами
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Выбираем 3 случайных вопроса из банка
        selectedQuestions = QuestionBank.getRandomQuestions(3);
        answerFields = new ArrayList<>();

        for (String question : selectedQuestions) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(question));
            JTextField answerField = new JTextField(15);
            answerFields.add(answerField);
            row.add(answerField);
            questionsPanel.add(row);
        }

        // Кнопка регистрации
        JPanel bottomPanel = new JPanel();
        JButton registerButton = new JButton("Зарегистрировать");
        registerButton.addActionListener(e -> register());
        bottomPanel.add(registerButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void register() {
        String login = loginField.getText().trim();
        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (UserDatabase.findUser(login) != null) {
            JOptionPane.showMessageDialog(this, "Пользователь с таким логином уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<QuestionAnswer> qaList = new ArrayList<>();
        for (int i = 0; i < selectedQuestions.size(); i++) {
            String answer = answerFields.get(i).getText().trim();
            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ответ на вопрос не может быть пустым", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            qaList.add(new QuestionAnswer(selectedQuestions.get(i), answer));
        }

        // Генерация одного пароля требуемой длины
        String generatedPassword = passwordGenerator.generatePasswords(1, passwordLength).get(0);

        UserAccount newUser = new UserAccount(login, qaList);
        UserDatabase.addUser(newUser);

        JOptionPane.showMessageDialog(this,
                "Пользователь успешно зарегистрирован!\nВаш пароль: " + generatedPassword,
                "Регистрация завершена",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}