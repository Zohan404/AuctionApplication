package elte.icj06o.auction.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PaillierKey {
    private final BigInteger n;
    private final BigInteger g;
    private final BigInteger nSquared;

    private final BigInteger lambda;
    private final BigInteger mu;

    private final SecureRandom random;

    public PaillierKey(int bits) {
        random = new SecureRandom();

        BigInteger p = BigInteger.probablePrime(bits / 2, random);
        BigInteger q = BigInteger.probablePrime(bits / 2, random);

        n = p.multiply(q);
        nSquared = n.multiply(n);

        BigInteger p1 = p.subtract(BigInteger.ONE);
        BigInteger q1 = q.subtract(BigInteger.ONE);
        BigInteger gcd = p1.gcd(q1);
        lambda = p1.multiply(q1).divide(gcd);

        g = n.add(BigInteger.ONE);

        BigInteger gLambda = g.modPow(lambda, nSquared);
        BigInteger L = L(gLambda);
        mu = L.modInverse(n);
    }

    private BigInteger L(BigInteger x) {
        return x.subtract(BigInteger.ONE).divide(n);
    }

    public BigInteger encrypt(BigInteger m) {
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0) {
            m = m.mod(n);
        }

        BigInteger r;
        do {
            r = new BigInteger(n.bitLength(), random);
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(n) >= 0);

        BigInteger gm = g.modPow(m, nSquared);
        BigInteger rn = r.modPow(n, nSquared);

        return gm.multiply(rn).mod(nSquared);
    }

    public BigInteger decrypt(BigInteger c) {
        BigInteger cLambda = c.modPow(lambda, nSquared);
        BigInteger L = L(cLambda);
        return L.multiply(mu).mod(n);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getNSquared() {
        return nSquared;
    }
}
