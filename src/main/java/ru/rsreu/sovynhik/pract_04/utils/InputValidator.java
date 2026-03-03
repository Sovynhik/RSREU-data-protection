package ru.rsreu.sovynhik.pract_04.utils;

import javax.swing.*;

public class InputValidator {

    public static ValidationResult validate(String pText, String vText, String tText, String userCountText) {
        try {
            double P = parseProbability(pText);
            if (P <= 0 || P >= 1) {
                return ValidationResult.error("Вероятность P должна быть в интервале (0, 1)");
            }

            double V = Double.parseDouble(vText.trim());
            if (V <= 0) {
                return ValidationResult.error("Скорость V должна быть положительной");
            }

            int T = Integer.parseInt(tText.trim());
            if (T <= 0) {
                return ValidationResult.error("Срок T должен быть положительным");
            }

            int userCount = Integer.parseInt(userCountText.trim());
            if (userCount <= 0) {
                return ValidationResult.error("Количество пользователей должно быть положительным");
            }

            return ValidationResult.success(P, V, T, userCount);

        } catch (NumberFormatException e) {
            return ValidationResult.error("Ошибка формата числа. Проверьте ввод.");
        }
    }

    private static double parseProbability(String text) throws NumberFormatException {
        text = text.trim().replace(',', '.');
        // Поддержка формата 10^-4
        if (text.contains("^")) {
            String[] parts = text.split("\\^");
            if (parts.length == 2) {
                double base = Double.parseDouble(parts[0]);
                double exponent = Double.parseDouble(parts[1]);
                return Math.pow(base, exponent);
            }
        }
        return Double.parseDouble(text);
    }

    public static class ValidationResult {
        public final boolean success;
        public final String errorMessage;
        public final double P;
        public final double V;
        public final int T;
        public final int userCount;

        private ValidationResult(boolean success, String errorMessage,
                                 double P, double V, int T, int userCount) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.P = P;
            this.V = V;
            this.T = T;
            this.userCount = userCount;
        }

        public static ValidationResult success(double P, double V, int T, int userCount) {
            return new ValidationResult(true, null, P, V, T, userCount);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message, 0, 0, 0, 0);
        }
    }
}