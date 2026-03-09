package ru.rsreu.sovynhik.pract_04.ui;

import ru.rsreu.sovynhik.pract_04.model.QuestionAnswer;
import ru.rsreu.sovynhik.pract_04.model.UserDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AuthDialog extends JDialog {
    private final JTextField loginField;
    private final JPanel questionsPanel;
    private List<QuestionAnswer> selectedQuestions;
    private List<JTextField> answerFields;
    private int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 3;

    public AuthDialog(JFrame parent) {
        super(parent, "Метод запрос-ответ", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Панель ввода логина
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Логин:"));
        loginField = new JTextField(15);
        topPanel.add(loginField);
        add(topPanel, BorderLayout.NORTH);

        // Панель для вопросов
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель с кнопками
        JPanel bottomPanel = new JPanel();
        JButton startButton = new JButton("Начать");
        startButton.addActionListener(e -> startAuthentication());
        bottomPanel.add(startButton);

        JButton checkButton = new JButton("Проверить");
        checkButton.addActionListener(e -> checkAnswers());
        bottomPanel.add(checkButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startAuthentication() {
        String login = loginField.getText().trim();
        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (UserDatabase.findUser(login) == null) {
            JOptionPane.showMessageDialog(this, "Пользователь не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedQuestions = UserDatabase.getRandomQuestionsForUser(login, 3);
        answerFields = new ArrayList<>();

        questionsPanel.removeAll();
        for (QuestionAnswer qa : selectedQuestions) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(qa.question()));
            JTextField answerField = new JTextField(15);
            answerFields.add(answerField);
            row.add(answerField);
            questionsPanel.add(row);
        }
        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    private void checkAnswers() {
        if (selectedQuestions == null || answerFields == null || selectedQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Сначала нажмите 'Начать'", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean allCorrect = true;
        for (int i = 0; i < selectedQuestions.size(); i++) {
            QuestionAnswer qa = selectedQuestions.get(i);
            String userAnswer = answerFields.get(i).getText();
            if (!UserDatabase.checkAnswer(qa, userAnswer)) {
                allCorrect = false;
                break;
            }
        }

        if (allCorrect) {
            JOptionPane.showMessageDialog(this, "Доступ разрешён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            attemptCount++;
            if (attemptCount >= MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(this,
                        "Превышено число попыток. Доступ заблокирован на 30 секунд.",
                        "Блокировка", JOptionPane.WARNING_MESSAGE);
                setEnabled(false); // блокируем диалог
                new Thread(() -> {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } finally {
                        SwingUtilities.invokeLater(() -> dispose());
                    }
                }).start();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Неверные ответы. Осталось попыток: " + (MAX_ATTEMPTS - attemptCount),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}