package ru.rsreu.sovynhik.pract_04.model;

import java.util.List;

public class UserAccount {
    private final String login;
    private final List<QuestionAnswer> questions;

    public UserAccount(String login, List<QuestionAnswer> questions) {
        this.login = login;
        this.questions = questions;
    }

    public String getLogin() {
        return login;
    }

    public List<QuestionAnswer> getQuestions() {
        return questions;
    }
}
