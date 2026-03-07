package ru.rsreu.sovynhik.pract_04.model;

public class QuestionAnswer {
    private final String question;
    private final String answer;

    public QuestionAnswer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
