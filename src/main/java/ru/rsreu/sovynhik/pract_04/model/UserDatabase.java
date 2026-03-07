package ru.rsreu.sovynhik.pract_04.model;

import java.util.*;

public class UserDatabase {
    // Карта: логин -> учётная запись пользователя
    private static final Map<String, UserAccount> users = new HashMap<>();

    static {
        // Создаём учётные записи с индивидуальными вопросами
        List<QuestionAnswer> user1Questions = Arrays.asList(
                new QuestionAnswer("Ваш любимый цвет?", "синий"),
                new QuestionAnswer("Город рождения?", "Москва"),
                new QuestionAnswer("Кличка домашнего питомца?", "Барсик")
        );
        users.put("user1", new UserAccount("user1", user1Questions));

        List<QuestionAnswer> user2Questions = Arrays.asList(
                new QuestionAnswer("Любимый фильм?", "Матрица"),
                new QuestionAnswer("Марка первого автомобиля?", "Лада"),
                new QuestionAnswer("Любимое блюдо?", "борщ")
        );
        users.put("user2", new UserAccount("user2", user2Questions));

        List<QuestionAnswer> adminQuestions = Arrays.asList(
                new QuestionAnswer("Кодовое слово администратора?", "admin123"),
                new QuestionAnswer("Дата основания компании?", "1991"),
                new QuestionAnswer("Имя первого руководителя?", "Иван")
        );
        users.put("admin", new UserAccount("admin", adminQuestions));
    }

    // Поиск пользователя по логину
    public static UserAccount findUser(String login) {
        return users.get(login);
    }

    // Получение случайных вопросов для пользователя
    public static List<QuestionAnswer> getRandomQuestionsForUser(String login, int count) {
        UserAccount user = findUser(login);
        if (user == null) {
            return Collections.emptyList();
        }
        List<QuestionAnswer> shuffled = new ArrayList<>(user.getQuestions());
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    // Проверка ответа (регистронезависимая)
    public static boolean checkAnswer(QuestionAnswer qa, String userAnswer) {
        return qa.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
    }
}