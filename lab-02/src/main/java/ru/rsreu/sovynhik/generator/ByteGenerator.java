package ru.rsreu.sovynhik.generator;

/**
 * Генератор псевдослучайных байтов (0..255).
 */
public interface ByteGenerator {
    /**
     * Возвращает следующий байт гаммы (0..255).
     */
    int nextByte();
}
