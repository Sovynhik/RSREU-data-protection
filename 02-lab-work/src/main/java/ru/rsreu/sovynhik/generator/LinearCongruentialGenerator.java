package ru.rsreu.sovynhik.generator;

import ru.rsreu.sovynhik.model.GeneratorParameters;

/**
 * Линейный конгруэнтный генератор (LCG) для получения псевдослучайных байтов.
 * Формула: T_{i} = (a * T_{i-1} + c) mod m
 * Гарантирует, что возвращаемое значение всегда в диапазоне [0, 255].
 */
public class LinearCongruentialGenerator implements ByteGenerator {
    private final long a;
    private final long c;
    private final long m;
    private long state;

    public LinearCongruentialGenerator(GeneratorParameters params) {
        this.a = params.a();
        this.c = params.c();
        this.m = params.m();
        this.state = params.seed() % m; // начальное состояние в пределах модуля
    }

    @Override
    public int nextByte() {
        state = (a * state + c) % m;
        // гарантированно 0..255, т.к. m <= 256 (для b=8)
        return (int) state;
    }
}
