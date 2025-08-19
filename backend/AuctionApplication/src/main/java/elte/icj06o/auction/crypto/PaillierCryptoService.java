package elte.icj06o.auction.crypto;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PaillierCryptoService {
    public final PaillierKey paillierKey;

    public PaillierCryptoService() {
        this.paillierKey = new PaillierKey(1024);
    }

    public boolean isGreater(BigInteger cipherA, BigInteger cipherB) {
        BigInteger a = decrypt(cipherA);
        BigInteger b = decrypt(cipherB);
        return a.compareTo(b) > 0;
    }

    public BigInteger decrypt(BigInteger cipherText) {
        return paillierKey.decrypt(cipherText);
    }

    public String encrypt(int value) {
        return paillierKey.encrypt(BigInteger.valueOf(value)).toString();
    }

    public PublicKeyData getPublicKeyData() {
        return new PublicKeyData(
                paillierKey.getN().toString(),
                paillierKey.getG().toString(),
                paillierKey.getNSquared().toString()
        );
    }

    public static class PublicKeyData {
        private final String n;
        private final String g;
        private final String nSquared;

        public PublicKeyData(String n, String g, String nSquared) {
            this.n = n;
            this.g = g;
            this.nSquared = nSquared;
        }

        public String getN() {
            return n;
        }

        public String getG() {
            return g;
        }

        public String getSquared() {
            return nSquared;
        }
    }
}
