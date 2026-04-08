package ru.rsreu.sovynhik.exception;

public class EncryptionException extends RSAException {
    public EncryptionException(String message) {
        super(message);
    }
}