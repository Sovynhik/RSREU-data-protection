package ru.rsreu.sovynhik.pract_04.model;

import java.util.List;

public record UserAccount(String login, List<QuestionAnswer> questions) {
}
