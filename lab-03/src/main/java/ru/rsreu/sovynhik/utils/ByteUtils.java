package ru.rsreu.sovynhik.utils;

import java.nio.charset.StandardCharsets;

public class ByteUtils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Преобразует строку в массив байт (UTF-8) и дополняет или обрезает до нужной длины.
     * @param str исходная строка
     * @param length требуемая длина массива
     * @return массив байт указанной длины
     */
    public static byte[] stringToFixedBytes(String str, int length) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[length];
        System.arraycopy(strBytes, 0, result, 0, Math.min(strBytes.length, length));
        return result;
    }

    /**
     * Конвертирует 4 байта (big-endian) в int.
     */
    public static int bytesToInt(byte[] b, int offset) {
        return ((b[offset] & 0xFF) << 24) |
                ((b[offset + 1] & 0xFF) << 16) |
                ((b[offset + 2] & 0xFF) << 8) |
                (b[offset + 3] & 0xFF);
    }

    /**
     * Конвертирует int в 4 байта (big-endian).
     */
    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    /**
     * Циклический сдвиг влево для 32-битного числа.
     */
    public static int rotl(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    /**
     * Циклический сдвиг вправо для 32-битного числа.
     */
    public static int rotr(int value, int bits) {
        return (value >>> bits) | (value << (32 - bits));
    }
}
