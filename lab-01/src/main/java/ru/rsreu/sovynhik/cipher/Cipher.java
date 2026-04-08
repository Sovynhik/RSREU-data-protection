package ru.rsreu.sovynhik.cipher;

public interface Cipher {
    String encrypt(String input);
    String decrypt(String input);
}