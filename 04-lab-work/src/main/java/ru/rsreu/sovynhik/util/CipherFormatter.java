package ru.rsreu.sovynhik.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CipherFormatter {

    public static String formatCiphertext(List<BigInteger> ciphertext) {
        StringBuilder sb = new StringBuilder();
        for (BigInteger bi : ciphertext) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(bi.toString());
        }
        return sb.toString();
    }

    public static List<BigInteger> parseCiphertext(String ciphertext) {
        if (ciphertext == null || ciphertext.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] parts = ciphertext.trim().split("\\s+");
        List<BigInteger> result = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.add(new BigInteger(part));
            }
        }
        return result;
    }
}
