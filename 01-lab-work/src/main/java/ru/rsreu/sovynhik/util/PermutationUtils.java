package ru.rsreu.sovynhik.util;

public class PermutationUtils {

    /**
     * Преобразует строку с числами, разделёнными запятыми или пробелами, в массив int.
     * @param text входная строка (например, "2,5,3,4,1,6" или "2 5 3 4 1 6")
     * @return массив целых чисел
     * @throws NumberFormatException если встретится нечисловое значение
     */
    public static int[] parsePermutation(String text) throws NumberFormatException {
        String[] parts = text.split("[,\\s]+");
        int[] perm = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            perm[i] = Integer.parseInt(parts[i].trim());
        }
        return perm;
    }

    /**
     * Проверяет, является ли массив корректной перестановкой чисел от 1 до N.
     * @param perm массив для проверки
     * @return true, если массив содержит все числа от 1 до perm.length без повторений
     */
    public static boolean isValidPermutation(int[] perm) {
        boolean[] seen = new boolean[perm.length];
        for (int num : perm) {
            if (num < 1 || num > perm.length) return false;
            if (seen[num - 1]) return false;
            seen[num - 1] = true;
        }
        return true;
    }
}
