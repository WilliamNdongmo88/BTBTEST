package will.dev.BTBTEST.security;

import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;

public class KeyGeneratorUtil {
    public static String generateEncryptionKey(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[byteLength];
        random.nextBytes(keyBytes);
        return Hex.encodeHexString(keyBytes);
    }
}
