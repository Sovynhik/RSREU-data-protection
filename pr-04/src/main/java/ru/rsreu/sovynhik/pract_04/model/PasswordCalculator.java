package ru.rsreu.sovynhik.pract_04.model;

public class PasswordCalculator {
    private final double P;          // вероятность подбора
    private final double V;          // скорость перебора (паролей/минута)
    private final int T;             // срок действия (дней)
    private final int alphabetSize;  // мощность алфавита

    private long S_star;
    private int minLength;

    public PasswordCalculator(double P, double V, int T, int alphabetSize) {
        this.P = P;
        this.V = V;
        this.T = T;
        this.alphabetSize = alphabetSize;
        calculate();
    }

    private void calculate() {
        long Tminutes = T * 24L * 60L;
        double totalAttempts = V * Tminutes;
        S_star = (long) Math.ceil(totalAttempts / P);
        int calculatedLength = (int) Math.ceil(Math.log(S_star) / Math.log(alphabetSize));
        minLength = Math.max(calculatedLength, 6);
    }

    public long getS_star() {
        return S_star;
    }

    public int getMinLength() {
        return minLength;
    }
}