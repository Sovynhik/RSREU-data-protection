package ru.rsreu.sovynhik.generator;

import ru.rsreu.sovynhik.model.GeneratorParameters;

public class LinearCongruentialGenerator implements ByteGenerator {
    private final long a;
    private final long c;
    private final long m;
    private long state;

    public LinearCongruentialGenerator(GeneratorParameters params) {
        this.a = params.a();
        this.c = params.c();
        this.m = params.m();
        this.state = params.seed() % m;
    }

    @Override
    public int nextByte() {
        state = (a * state + c) % m;
        return (int) state;
    }
}
