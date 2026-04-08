package ru.rsreu.sovynhik.pract_04.model;

import java.util.Arrays;
import java.util.List;

public class QuestionBank {
    public static final List<String> QUESTIONS = Arrays.asList(
            "Ваш любимый цвет?",
            "Город рождения?",
            "Кличка домашнего питомца?",
            "Любимый фильм?",
            "Марка первого автомобиля?",
            "Любимое блюдо?",
            "Имя лучшего друга?",
            "Ваша любимая книга?",
            "Название улицы, где вы росли?",
            "Самый памятный год?"
    );

    // Метод для получения случайных вопросов
    public static List<String> getRandomQuestions(int count) {
        // Перемешиваем и берём первые count
        List<String> shuffled = new java.util.ArrayList<>(QUESTIONS);
        java.util.Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}
