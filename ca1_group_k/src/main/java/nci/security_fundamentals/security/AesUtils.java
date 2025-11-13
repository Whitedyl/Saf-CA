package nci.security_fundamentals.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Author Jordan Carthy
 *
 * AesUtils encrypts and decrypts messages using AES-128 in CBC mode.
 * Each message gets a random IV and is transmitted as Base64(IV):Base64(ciphertext).
 */
public class AesUtils {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static SecretKey generateKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(128);
        return gen.generateKey();
    }

    public static SecretKey fromBase64(String base64Key) {
        return new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");
    }

    private static String base64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static String encrypt(SecretKey key, String message) throws Exception {
        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encrypted = cipher.doFinal(message.getBytes());
        return base64(iv) + ":" + base64(encrypted);
    }

    public static String decrypt(SecretKey key, String data) throws Exception {
        String[] parts = data.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted);
    }
}
