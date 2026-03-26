package ru.rsreu.sovynhik.util;

import ru.rsreu.sovynhik.exception.KeyGenerationException;

import java.math.BigInteger;

public class KeyValidator {

    public static void validatePrimes(BigInteger p, BigInteger q) {
        if (p == null || q == null) {
            throw new KeyGenerationException("p and q cannot be null");
        }
        if (!p.isProbablePrime(100) || !q.isProbablePrime(100)) {
            throw new KeyGenerationException("p and q must be prime numbers");
        }
        if (p.equals(BigInteger.ONE) || q.equals(BigInteger.ONE)) {
            throw new KeyGenerationException("p and q cannot be 1");
        }
        if (p.equals(q)) {
            throw new KeyGenerationException("p and q should be different primes");
        }
    }
}
