package ru.rsreu.sovynhik.core;

import ru.rsreu.sovynhik.exception.EncryptionException;
import ru.rsreu.sovynhik.exception.KeyGenerationException;
import ru.rsreu.sovynhik.model.RSAKeySet;
import ru.rsreu.sovynhik.util.KeyValidator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RSAEngineImpl implements RSAEngine {

    @Override
    public RSAKeySet generateKeys(BigInteger p, BigInteger q) {
        KeyValidator.validatePrimes(p, q);

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(2);
        while (e.compareTo(phi) < 0) {
            if (e.gcd(phi).equals(BigInteger.ONE)) {
                break;
            }
            e = e.add(BigInteger.ONE);
        }

        if (e.compareTo(phi) >= 0) {
            throw new KeyGenerationException("Не удалось подобрать e (φ слишком мало)");
        }

        BigInteger d = e.modInverse(phi);
        return new RSAKeySet(e, d, n, p, q);
    }

    @Override
    public List<BigInteger> encrypt(String plaintext, BigInteger e, BigInteger n) {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new EncryptionException("Исходное сообщение не может быть пустым");
        }
        if (e == null || n == null) {
            throw new EncryptionException("Открытый ключ (e, n) не может быть null");
        }

        List<BigInteger> result = new ArrayList<>();
        for (int i = 0; i < plaintext.length(); i++) {
            char ch = plaintext.charAt(i);
            BigInteger m = BigInteger.valueOf(ch);
            if (m.compareTo(n) >= 0) {
                throw new EncryptionException(
                        "Символ '" + ch + "' с кодом " + (int) ch + " превышает n. Увеличьте p и q.");
            }
            result.add(m.modPow(e, n));
        }
        return result;
    }

    @Override
    public String decrypt(List<BigInteger> ciphertext, BigInteger d, BigInteger n) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            throw new EncryptionException("Зашифрованный текст не может быть пустым");
        }
        if (d == null || n == null) {
            throw new EncryptionException("Закрытый ключ (d, n) не может быть null");
        }

        StringBuilder plain = new StringBuilder();
        for (BigInteger c : ciphertext) {
            if (c.compareTo(n) >= 0) {
                throw new EncryptionException("Число " + c + " больше или равно n. Возможно, неверный ключ.");
            }
            BigInteger m = c.modPow(d, n);
            int code = m.intValue();
            if (code < 0 || code > 0xFFFF) {
                throw new EncryptionException("Получен некорректный код символа: " + code);
            }
            plain.append((char) code);
        }
        return plain.toString();
    }
}