package ru.rsreu.sovynhik.model;

/**
 * Параметры линейного конгруэнтного генератора.
 *
 * @param a     множитель
 * @param c     приращение
 * @param seed  начальное значение (T0)
 * @param m     модуль (2^b)
 */
public record GeneratorParameters(long a, long c, long seed, long m) { }
