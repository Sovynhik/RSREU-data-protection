package ru.rsreu.sovynhik.pract_04.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!\"#$%&'";

    public static final String FULL_ALPHABET = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;
    public static final int ALPHABET_SIZE = FULL_ALPHABET.length();

    private final SecureRandom random = new SecureRandom();

    public List<String> generatePasswords(int count, int length) {
        List<String> passwords = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            passwords.add(generatePassword(length));
        }
        return passwords;
    }

    private String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHABET_SIZE);
            sb.append(FULL_ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}
