package ru.rsreu.sovynhik.model;

import java.math.BigInteger;

public class RSAKeySet {
    private final BigInteger e;  // public exponent
    private final BigInteger d;  // private exponent
    private final BigInteger n;  // modulus
    private final BigInteger p;  // prime factor (for display)
    private final BigInteger q;  // prime factor (for display)

    public RSAKeySet(BigInteger e, BigInteger d, BigInteger n, BigInteger p, BigInteger q) {
        this.e = e;
        this.d = d;
        this.n = n;
        this.p = p;
        this.q = q;
    }

    public BigInteger getE() { return e; }
    public BigInteger getD() { return d; }
    public BigInteger getN() { return n; }
    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }
}
