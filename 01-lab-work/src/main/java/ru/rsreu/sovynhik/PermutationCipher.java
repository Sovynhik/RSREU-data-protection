package ru.rsreu.sovynhik;

import java.nio.charset.StandardCharsets;

/**
 * Шифр перестановки (транспозиции) с фиксированным размером блока.
 * Перестановка задаётся массивом, где индекс (начиная с 0) соответствует
 * исходной позиции символа в блоке, а значение (начиная с 1) — новой позиции.
 * Пример для таблицы: [2, 5, 3, 4, 1, 6] означает, что символ с исходной позиции 1
 * переходит на позицию 2, с позиции 2 → 5 и т.д.
 */
public class PermutationCipher implements Cipher {
    private final int[] perm;          // прямая перестановка (значения от 1 до n)
    private final int[] invPerm;        // обратная перестановка
    private final int blockSize;

    /**
     * @param perm массив перестановки длины n, где perm[i] = новая позиция (1..n)
     *             для символа, стоявшего на позиции i+1.
     */
    public PermutationCipher(int[] perm) {
        this.perm = perm.clone();
        this.blockSize = perm.length;
        this.invPerm = new int[blockSize];
        // построение обратной перестановки: invPerm[new-1] = old
        for (int i = 0; i < blockSize; i++) {
            int newPos = perm[i] - 1; // индекс в выходном блоке (0..n-1)
            invPerm[newPos] = i;      // на эту позицию ставится символ из i
        }
    }

    @Override
    public String encrypt(String input) {
        // Разбиваем входную строку на блоки по blockSize символов,
        // дополняя последний блок пробелами при необходимости.
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        int len = bytes.length;
        int numBlocks = (len + blockSize - 1) / blockSize;
        byte[] result = new byte[numBlocks * blockSize];

        for (int b = 0; b < numBlocks; b++) {
            int start = b * blockSize;
            // Копируем исходный блок (или часть) во временный массив
            byte[] block = new byte[blockSize];
            for (int i = 0; i < blockSize; i++) {
                int srcIdx = start + i;
                if (srcIdx < len) {
                    block[i] = bytes[srcIdx];
                } else {
                    block[i] = ' '; // дополнение пробелом
                }
            }
            // Применяем прямую перестановку
            byte[] encBlock = new byte[blockSize];
            for (int i = 0; i < blockSize; i++) {
                int newPos = perm[i] - 1;   // куда ставим символ с исходной позиции i
                encBlock[newPos] = block[i];
            }
            // Помещаем результат в общий массив
            System.arraycopy(encBlock, 0, result, b * blockSize, blockSize);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    @Override
    public String decrypt(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        int len = bytes.length;
        // Длина должна быть кратна blockSize (если нет – обрежем или дополним, но лучше считать, что вход корректен)
        if (len % blockSize != 0) {
            // На всякий случай дополним до кратного размера пробелами (хотя encrypt выдаёт ровные блоки)
            int newLen = ((len + blockSize - 1) / blockSize) * blockSize;
            byte[] padded = new byte[newLen];
            System.arraycopy(bytes, 0, padded, 0, len);
            for (int i = len; i < newLen; i++) padded[i] = ' ';
            bytes = padded;
            len = newLen;
        }
        int numBlocks = len / blockSize;
        byte[] result = new byte[len];

        for (int b = 0; b < numBlocks; b++) {
            int start = b * blockSize;
            byte[] block = new byte[blockSize];
            System.arraycopy(bytes, start, block, 0, blockSize);
            // Применяем обратную перестановку
            byte[] decBlock = new byte[blockSize];
            for (int i = 0; i < blockSize; i++) {
                int originalPos = invPerm[i];   // исходная позиция, откуда пришёл символ, стоящий сейчас на i
                decBlock[originalPos] = block[i];
            }
            System.arraycopy(decBlock, 0, result, start, blockSize);
        }
        // Удаляем дополняющие пробелы в конце (если исходный текст был короче)
        String decrypted = new String(result, StandardCharsets.UTF_8);
        return decrypted.replaceAll("\\s+$", ""); // обрезаем концевые пробелы (можно уточнить)
    }
}