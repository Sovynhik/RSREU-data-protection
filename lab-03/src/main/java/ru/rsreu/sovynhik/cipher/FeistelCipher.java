package ru.rsreu.sovynhik.cipher;

import ru.rsreu.sovynhik.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FeistelCipher {

    private static int rol(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private static int ror(int value, int bits) {
        return (value >>> bits) | (value << (32 - bits));
    }

    private static int[] deriveKey(String keyStr) {
        byte[] keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);
        byte[] key8 = new byte[8];
        System.arraycopy(keyBytes, 0, key8, 0, Math.min(keyBytes.length, 8));
        ByteBuffer bb = ByteBuffer.wrap(key8).order(ByteOrder.BIG_ENDIAN);
        int k1 = bb.getInt();
        int k2 = bb.getInt();
        return new int[]{k1, k2};
    }

    private static byte[] encryptBlock(byte[] block, int[] keyParts, int rounds) {
        int k1 = keyParts[0];
        int k2 = keyParts[1];

        ByteBuffer bb = ByteBuffer.wrap(block).order(ByteOrder.BIG_ENDIAN);
        int x1 = bb.getInt();
        int x2 = bb.getInt();
        int x3 = bb.getInt();
        int x4 = bb.getInt();

        for (int i = 1; i < rounds; i++) {
            int vi = rol(k1, i) ^ ror(k2, i);
            int newX1 = x2 ^ vi;
            int newX2 = x3;
            int newX3 = x4;
            int newX4 = x1;
            x1 = newX1;
            x2 = newX2;
            x3 = newX3;
            x4 = newX4;
        }

        int newX1 = x2;
        int newX2 = x3;
        int newX3 = x4;
        int newX4 = x1;
        x1 = newX1;
        x2 = newX2;
        x3 = newX3;
        x4 = newX4;

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        out.putInt(x1).putInt(x2).putInt(x3).putInt(x4);
        return out.array();
    }

    private static byte[] decryptBlock(byte[] block, int[] keyParts, int rounds) {
        int k1 = keyParts[0];
        int k2 = keyParts[1];

        ByteBuffer bb = ByteBuffer.wrap(block).order(ByteOrder.BIG_ENDIAN);
        int x1 = bb.getInt();
        int x2 = bb.getInt();
        int x3 = bb.getInt();
        int x4 = bb.getInt();

        int newX10 = x4;
        int newX20 = x1;
        int newX30 = x2;
        int newX40 = x3;
        x1 = newX10;
        x2 = newX20;
        x3 = newX30;
        x4 = newX40;

        for (int i = rounds - 1; i >= 1; i--) {
            int vi = rol(k1, i) ^ ror(k2, i);
            int newX1 = x4;
            int newX2 = x1 ^ vi;
            int newX3 = x2;
            int newX4 = x3;
            x1 = newX1;
            x2 = newX2;
            x3 = newX3;
            x4 = newX4;
        }

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        out.putInt(x1).putInt(x2).putInt(x3).putInt(x4);
        return out.array();
    }

    private static byte[] padToLength(byte[] input, int length) {
        return Arrays.copyOf(input, length);
    }

    private static byte[] trimTrailingZeros(byte[] input) {
        int i = input.length - 1;
        while (i >= 0 && input[i] == 0) {
            i--;
        }
        return Arrays.copyOf(input, i + 1);
    }

    public static String encryptText(String plaintext, String keyStr, int rounds) {
        int[] keyParts = deriveKey(keyStr);
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        int blockSize = 16;
        int len = plainBytes.length;
        int numBlocks = (len + blockSize - 1) / blockSize;
        byte[] cipherBytes = new byte[numBlocks * blockSize];

        for (int i = 0; i < numBlocks; i++) {
            int start = i * blockSize;
            int end = Math.min(start + blockSize, len);
            byte[] block = Arrays.copyOfRange(plainBytes, start, end);
            block = padToLength(block, blockSize);
            byte[] encrypted = encryptBlock(block, keyParts, rounds);
            System.arraycopy(encrypted, 0, cipherBytes, start, blockSize);
        }
        return ByteUtils.bytesToHex(cipherBytes);
    }

    public static String decryptText(String cipherHex, String keyStr, int rounds) {
        int[] keyParts = deriveKey(keyStr);
        byte[] cipherBytes = ByteUtils.hexToBytes(cipherHex);
        int blockSize = 16;
        if (cipherBytes.length % blockSize != 0) {
            throw new IllegalArgumentException("Длина шифрограммы не кратна 16 байтам");
        }
        int numBlocks = cipherBytes.length / blockSize;
        byte[] decryptedBytes = new byte[numBlocks * blockSize];

        for (int i = 0; i < numBlocks; i++) {
            int start = i * blockSize;
            byte[] block = Arrays.copyOfRange(cipherBytes, start, start + blockSize);
            byte[] decrypted = decryptBlock(block, keyParts, rounds);
            System.arraycopy(decrypted, 0, decryptedBytes, start, blockSize);
        }

        byte[] trimmed = trimTrailingZeros(decryptedBytes);
        return new String(trimmed, StandardCharsets.UTF_8);
    }

    private static String intToHex(int val) {
        return String.format("%08x", val);
    }

    private static String encryptBlockWithLog(byte[] block, int[] keyParts, int rounds, int blockIndex) {
        int k1 = keyParts[0];
        int k2 = keyParts[1];

        ByteBuffer bb = ByteBuffer.wrap(block).order(ByteOrder.BIG_ENDIAN);
        int x1 = bb.getInt();
        int x2 = bb.getInt();
        int x3 = bb.getInt();
        int x4 = bb.getInt();

        StringBuilder log = new StringBuilder();
        log.append("===== Блок ").append(blockIndex).append(" =====\n");
        log.append("Исходный блок (hex): ").append(ByteUtils.bytesToHex(block)).append("\n");
        log.append("Разделение на части:\n");
        log.append("x1 = ").append(intToHex(x1)).append("\n");
        log.append("x2 = ").append(intToHex(x2)).append("\n");
        log.append("x3 = ").append(intToHex(x3)).append("\n");
        log.append("x4 = ").append(intToHex(x4)).append("\n");
        log.append("Ключевые части: k1=").append(intToHex(k1)).append(", k2=").append(intToHex(k2)).append("\n\n");

        for (int i = 1; i < rounds; i++) {
            int vi = rol(k1, i) ^ ror(k2, i);
            int newX1 = x2 ^ vi;
            int newX2 = x3;
            int newX3 = x4;
            int newX4 = x1;

            log.append("Раунд ").append(i).append(":\n");
            log.append("  vi = rol(k1,").append(i).append(") XOR ror(k2,").append(i).append(") = ")
                    .append(intToHex(rol(k1,i))).append(" XOR ").append(intToHex(ror(k2,i))).append(" = ").append(intToHex(vi)).append("\n");
            log.append("  f = vi\n");
            log.append("  newX1 = x2 XOR f = ").append(intToHex(x2)).append(" XOR ").append(intToHex(vi)).append(" = ").append(intToHex(newX1)).append("\n");
            log.append("  newX2 = x3 = ").append(intToHex(newX2)).append("\n");
            log.append("  newX3 = x4 = ").append(intToHex(newX3)).append("\n");
            log.append("  newX4 = x1 = ").append(intToHex(newX4)).append("\n\n");

            x1 = newX1;
            x2 = newX2;
            x3 = newX3;
            x4 = newX4;
        }

        log.append("Последний раунд:\n");
        log.append("  newX1 = x2 = ").append(intToHex(x2)).append("\n");
        log.append("  newX2 = x3 = ").append(intToHex(x3)).append("\n");
        log.append("  newX3 = x4 = ").append(intToHex(x4)).append("\n");
        log.append("  newX4 = x1 = ").append(intToHex(x1)).append("\n\n");

        int newX1 = x2;
        int newX2 = x3;
        int newX3 = x4;
        int newX4 = x1;
        x1 = newX1;
        x2 = newX2;
        x3 = newX3;
        x4 = newX4;

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        out.putInt(x1).putInt(x2).putInt(x3).putInt(x4);
        byte[] encrypted = out.array();
        log.append("Зашифрованный блок: ").append(ByteUtils.bytesToHex(encrypted)).append("\n");
        return log.toString();
    }

    private static String decryptBlockWithLog(byte[] block, int[] keyParts, int rounds, int blockIndex) {
        int k1 = keyParts[0];
        int k2 = keyParts[1];

        ByteBuffer bb = ByteBuffer.wrap(block).order(ByteOrder.BIG_ENDIAN);
        int x1 = bb.getInt();
        int x2 = bb.getInt();
        int x3 = bb.getInt();
        int x4 = bb.getInt();

        StringBuilder log = new StringBuilder();
        log.append("===== Блок ").append(blockIndex).append(" (дешифрование) =====\n");
        log.append("Шифроблок (hex): ").append(ByteUtils.bytesToHex(block)).append("\n");
        log.append("Разделение на части:\n");
        log.append("x1 = ").append(intToHex(x1)).append("\n");
        log.append("x2 = ").append(intToHex(x2)).append("\n");
        log.append("x3 = ").append(intToHex(x3)).append("\n");
        log.append("x4 = ").append(intToHex(x4)).append("\n\n");

        log.append("Обратная перестановка (отмена последнего раунда):\n");
        log.append("  newX1 = x4 = ").append(intToHex(x4)).append("\n");
        log.append("  newX2 = x1 = ").append(intToHex(x1)).append("\n");
        log.append("  newX3 = x2 = ").append(intToHex(x2)).append("\n");
        log.append("  newX4 = x3 = ").append(intToHex(x3)).append("\n\n");
        int newX10 = x4;
        int newX20 = x1;
        int newX30 = x2;
        int newX40 = x3;
        x1 = newX10;
        x2 = newX20;
        x3 = newX30;
        x4 = newX40;

        for (int i = rounds - 1; i >= 1; i--) {
            int vi = rol(k1, i) ^ ror(k2, i);
            int newX1 = x4;
            int newX2 = x1 ^ vi;
            int newX3 = x2;
            int newX4 = x3;

            log.append("Раунд ").append(i).append(" (обратный):\n");
            log.append("  vi = rol(k1,").append(i).append(") XOR ror(k2,").append(i).append(") = ")
                    .append(intToHex(rol(k1,i))).append(" XOR ").append(intToHex(ror(k2,i))).append(" = ").append(intToHex(vi)).append("\n");
            log.append("  f = vi\n");
            log.append("  newX2 = x1 XOR f = ").append(intToHex(x1)).append(" XOR ").append(intToHex(vi)).append(" = ").append(intToHex(newX2)).append("\n");
            log.append("  newX1 = x4 = ").append(intToHex(newX1)).append("\n");
            log.append("  newX3 = x2 = ").append(intToHex(newX3)).append("\n");
            log.append("  newX4 = x3 = ").append(intToHex(newX4)).append("\n\n");

            x1 = newX1;
            x2 = newX2;
            x3 = newX3;
            x4 = newX4;
        }

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        out.putInt(x1).putInt(x2).putInt(x3).putInt(x4);
        byte[] decrypted = out.array();
        log.append("Дешифрованный блок (с нулями): ").append(ByteUtils.bytesToHex(decrypted)).append("\n");
        log.append("Дешифрованный текст (UTF-8): ").append(new String(trimTrailingZeros(decrypted), StandardCharsets.UTF_8)).append("\n");
        return log.toString();
    }

    public static String encryptTextWithLog(String plaintext, String keyStr, int rounds) {
        int[] keyParts = deriveKey(keyStr);
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        int blockSize = 16;
        int len = plainBytes.length;
        int numBlocks = (len + blockSize - 1) / blockSize;
        StringBuilder fullLog = new StringBuilder();

        for (int i = 0; i < numBlocks; i++) {
            int start = i * blockSize;
            int end = Math.min(start + blockSize, len);
            byte[] block = Arrays.copyOfRange(plainBytes, start, end);
            fullLog.append("Исходный блок ").append(i+1).append(" (без дополнения): ").append(ByteUtils.bytesToHex(block)).append("\n");
            block = padToLength(block, blockSize);
            fullLog.append("После дополнения нулями: ").append(ByteUtils.bytesToHex(block)).append("\n");
            fullLog.append(encryptBlockWithLog(block, keyParts, rounds, i+1));
            fullLog.append("\n");
        }
        return fullLog.toString();
    }

    public static String decryptTextWithLog(String cipherHex, String keyStr, int rounds) {
        int[] keyParts = deriveKey(keyStr);
        byte[] cipherBytes = ByteUtils.hexToBytes(cipherHex);
        int blockSize = 16;
        if (cipherBytes.length % blockSize != 0) {
            throw new IllegalArgumentException("Длина шифрограммы не кратна 16 байтам");
        }
        int numBlocks = cipherBytes.length / blockSize;
        StringBuilder fullLog = new StringBuilder();

        for (int i = 0; i < numBlocks; i++) {
            int start = i * blockSize;
            byte[] block = Arrays.copyOfRange(cipherBytes, start, start + blockSize);
            fullLog.append("Блок ").append(i+1).append(" шифрограммы: ").append(ByteUtils.bytesToHex(block)).append("\n");
            fullLog.append(decryptBlockWithLog(block, keyParts, rounds, i+1));
            fullLog.append("\n");
        }
        return fullLog.toString();
    }
}