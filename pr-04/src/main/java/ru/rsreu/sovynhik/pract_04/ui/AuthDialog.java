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

    private final JButton startButton;
    private final JButton checkButton;

    private final JLabel timerLabel;
    private Timer countdownTimer;
    private int remainingSeconds = 5;

    public AuthDialog(JFrame parent) {
        super(parent, "Метод запрос-ответ", true);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Верхняя панель с логином и таймером (используем GridLayout)
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Панель для логина
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loginPanel.add(new JLabel("Логин:"));
        loginField = new JTextField(15);
        loginPanel.add(loginField);
        topPanel.add(loginPanel);

        // Панель для таймера (изначально скрыта)
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerLabel = new JLabel("Осталось: 30 сек.");
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 14));
        timerPanel.add(timerLabel);
        timerPanel.setVisible(false); // скрыта по умолчанию
        topPanel.add(timerPanel);

        add(topPanel, BorderLayout.NORTH);

        // Панель для вопросов
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель с кнопками
        JPanel bottomPanel = new JPanel();

        startButton = new JButton("Начать");
        startButton.addActionListener(e -> startAuthentication());
        bottomPanel.add(startButton);

        checkButton = new JButton("Проверить");
        checkButton.addActionListener(e -> checkAnswers());
        bottomPanel.add(checkButton);

        JButton exitButton = new JButton("Выйти");
        exitButton.addActionListener(e -> dispose());
        bottomPanel.add(exitButton);

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
                // Блокируем интерфейс
                setBlocked(true);

                // Показываем панель таймера
                JPanel topPanel = (JPanel) getContentPane().getComponent(0); // получаем верхнюю панель
                topPanel.getComponent(1).setVisible(true); // timerPanel

                // Сбрасываем счётчик
                remainingSeconds = 5;
                timerLabel.setText("Осталось: " + remainingSeconds + " сек.");

                if (countdownTimer != null && countdownTimer.isRunning()) {
                    countdownTimer.stop();
                }
                countdownTimer = new Timer(1000, e -> {
                    remainingSeconds--;
                    if (remainingSeconds > 0) {
                        timerLabel.setText("Осталось: " + remainingSeconds + " сек.");
                    } else {
                        countdownTimer.stop();
                        // Скрываем панель таймера
                        topPanel.getComponent(1).setVisible(false);
                        // Разблокируем интерфейс
                        setBlocked(false);
                        attemptCount = 0;
                    }
                });
                countdownTimer.start();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Неверные ответы. Осталось попыток: " + (MAX_ATTEMPTS - attemptCount),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setBlocked(boolean blocked) {
        loginField.setEnabled(!blocked);
        questionsPanel.setEnabled(!blocked);
        if (answerFields != null) {
            for (JTextField field : answerFields) {
                field.setEnabled(!blocked);
            }
        }
        startButton.setEnabled(!blocked);
        checkButton.setEnabled(!blocked);
    }

    @Override
    public void dispose() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        super.dispose();
    }
}