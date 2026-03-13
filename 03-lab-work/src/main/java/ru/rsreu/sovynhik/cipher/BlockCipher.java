package ru.rsreu.sovynhik.cipher;

public interface BlockCipher {
    /**
     * Шифрует один блок данных.
     * @param block блок данных (фиксированной длины, например 16 байт)
     * @return зашифрованный блок той же длины
     */
    byte[] encryptBlock(byte[] block);

    /**
     * Дешифрует один блок данных.
     * @param block зашифрованный блок
     * @return расшифрованный блок
     */
    byte[] decryptBlock(byte[] block);
}
