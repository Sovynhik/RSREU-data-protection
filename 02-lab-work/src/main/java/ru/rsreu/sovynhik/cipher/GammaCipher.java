package ru.rsreu.sovynhik.cipher;

import ru.rsreu.sovynhik.generator.ByteGenerator;

public class GammaCipher {
    public byte[] applyGamma(byte[] data, ByteGenerator generator) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ generator.nextByte());
        }
        return result;
    }

    public byte[] encrypt(byte[] data, ByteGenerator generator) {
        return applyGamma(data, generator);
    }

    public byte[] decrypt(byte[] data, ByteGenerator generator) {
        return applyGamma(data, generator);
    }
}
