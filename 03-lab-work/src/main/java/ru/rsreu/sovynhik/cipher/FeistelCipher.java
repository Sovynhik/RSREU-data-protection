package ru.rsreu.sovynhik.cipher;

import ru.rsreu.sovynhik.utils.ByteUtils;

import java.util.Arrays;

/**
 * Реализация модифицированной сети Фейштеля с 4 ветвями (блок 128 бит).
 * Ключ 64 бита разбивается на две 32-битные половинки K1 и K2.
 * Для каждого раунда i (1..rounds) вычисляется подключ Vi = (K1 <<< i) XOR (K2 >>> i).
 * Образующая функция F(Vi) = Vi (операция XOR).
 * Шифрование: для каждого раунда
 *   X1' = X2 XOR Vi
 *   X2' = X3
 *   X3' = X4
 *   X4' = X1
 * Дешифрование: обратный порядок раундов с теми же Vi.
 */
public class FeistelCipher {
    private final int rounds;
    private final int[] roundKeys; // массив Vi для каждого раунда (начиная с 1)

    /**
     * @param key ключ (8 байт, 64 бита). Если длина меньше 8, дополняется нулями.
     * @param rounds количество раундов (должно быть положительным)
     */
    public FeistelCipher(byte[] key, int rounds) {
        if (rounds <= 0) throw new IllegalArgumentException("Количество раундов должно быть > 0");
        this.rounds = rounds;

        // Подготовка ключа: берем первые 8 байт, остальное нули
        byte[] keyBytes = new byte[8];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, 8));

        // Разделяем на K1 и K2 (каждый по 4 байта, big-endian)
        int K1 = ByteUtils.bytesToInt(keyBytes, 0);
        int K2 = ByteUtils.bytesToInt(keyBytes, 4);

        // Вычисляем раундовые подключи
        roundKeys = new int[rounds + 1]; // индексы от 1 до rounds
        for (int i = 1; i <= rounds; i++) {
            roundKeys[i] = ByteUtils.rotl(K1, i) ^ ByteUtils.rotr(K2, i);
        }
    }

    /**
     * Шифрование одного блока (16 байт).
     * @param block входной блок (должен быть ровно 16 байт)
     * @return зашифрованный блок (16 байт)
     */
    public byte[] encryptBlock(byte[] block) {
        if (block.length != 16) throw new IllegalArgumentException("Блок должен быть 16 байт");

        // Разбиваем на 4 части по 4 байта
        int X1 = ByteUtils.bytesToInt(block, 0);
        int X2 = ByteUtils.bytesToInt(block, 4);
        int X3 = ByteUtils.bytesToInt(block, 8);
        int X4 = ByteUtils.bytesToInt(block, 12);

        for (int i = 1; i <= rounds; i++) {
            int Vi = roundKeys[i];
            int newX1 = X2 ^ Vi;
            int newX2 = X3;
            int newX3 = X4;
            int newX4 = X1;

            X1 = newX1;
            X2 = newX2;
            X3 = newX3;
            X4 = newX4;
        }

        // Собираем результат
        byte[] result = new byte[16];
        System.arraycopy(ByteUtils.intToBytes(X1), 0, result, 0, 4);
        System.arraycopy(ByteUtils.intToBytes(X2), 0, result, 4, 4);
        System.arraycopy(ByteUtils.intToBytes(X3), 0, result, 8, 4);
        System.arraycopy(ByteUtils.intToBytes(X4), 0, result, 12, 4);
        return result;
    }

    /**
     * Дешифрование одного блока (16 байт).
     * @param block зашифрованный блок
     * @return расшифрованный блок
     */
    public byte[] decryptBlock(byte[] block) {
        if (block.length != 16) throw new IllegalArgumentException("Блок должен быть 16 байт");

        int X1 = ByteUtils.bytesToInt(block, 0);
        int X2 = ByteUtils.bytesToInt(block, 4);
        int X3 = ByteUtils.bytesToInt(block, 8);
        int X4 = ByteUtils.bytesToInt(block, 12);

        for (int i = rounds; i >= 1; i--) {
            int Vi = roundKeys[i];
            int prevX1 = X4;
            int prevX2 = X1 ^ Vi;
            int prevX3 = X2;
            int prevX4 = X3;

            X1 = prevX1;
            X2 = prevX2;
            X3 = prevX3;
            X4 = prevX4;
        }

        byte[] result = new byte[16];
        System.arraycopy(ByteUtils.intToBytes(X1), 0, result, 0, 4);
        System.arraycopy(ByteUtils.intToBytes(X2), 0, result, 4, 4);
        System.arraycopy(ByteUtils.intToBytes(X3), 0, result, 8, 4);
        System.arraycopy(ByteUtils.intToBytes(X4), 0, result, 12, 4);
        return result;
    }

    /**
     * Шифрование произвольного текста.
     * Текст разбивается на блоки по 16 байт, последний дополняется нулями.
     * @param data исходные байты
     * @return зашифрованные байты (длина кратна 16)
     */
    public byte[] encrypt(byte[] data) {
        int padding = 16 - (data.length % 16);
        if (padding == 16) padding = 0; // не дополняем, если уже кратно
        byte[] padded = Arrays.copyOf(data, data.length + padding);
        byte[] result = new byte[padded.length];
        for (int i = 0; i < padded.length; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            byte[] encrypted = encryptBlock(block);
            System.arraycopy(encrypted, 0, result, i, 16);
        }
        return result;
    }

    /**
     * Дешифрование (длина данных должна быть кратна 16).
     * @param data зашифрованные байты
     * @return расшифрованные байты (включая возможные нули в конце)
     */
    public byte[] decrypt(byte[] data) {
        if (data.length % 16 != 0) throw new IllegalArgumentException("Длина зашифрованных данных должна быть кратна 16");
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i += 16) {
            byte[] block = Arrays.copyOfRange(data, i, i + 16);
            byte[] decrypted = decryptBlock(block);
            System.arraycopy(decrypted, 0, result, i, 16);
        }
        return result;
    }
}
