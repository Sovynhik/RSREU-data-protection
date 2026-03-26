package ru.rsreu.sovynhik.core;

import ru.rsreu.sovynhik.model.RSAKeySet;

import java.math.BigInteger;
import java.util.List;

public interface RSAEngine {
    RSAKeySet generateKeys(BigInteger p, BigInteger q);
    List<BigInteger> encrypt(String plaintext, BigInteger e, BigInteger n);
    String decrypt(List<BigInteger> ciphertext, BigInteger d, BigInteger n);
}