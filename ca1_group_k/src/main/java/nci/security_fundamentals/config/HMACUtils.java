package nci.security_fundamentals.config;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import nci.security_fundamentals.config.EnvConfig;

/**
 * @Author Dylan White
 * HMACUtils - Message integrity verification and tampering detection
 *
 * uses HMAC-SHA256 to generate cryptographic signatures for messages.
 * Ensures that messages have not been tampered or modifies during transit.
 *
 * Usage:
 * -generateHMAC(message) - creates the signature
 * -verifyHMAC(message, signature) - checks if the message matches the generated signature.
 *
 * if the signature doesn't match the message was tampered with and will be rejected.
 */
public class HMACUtils {

    private static final String ALGORITHM = "HmacSHA256";
    private static String HMAC_SECRET_KEY;

    static {
        try{
            HMAC_SECRET_KEY = EnvConfig.getRequired("HMAC_SECRET_KEY");
//            System.out.println("Loaded key from .env: " + HMAC_SECRET_KEY);
        } catch (Exception e){
            System.out.println("Failed to load key from .env: " + e.getMessage());
            e.printStackTrace();
            HMAC_SECRET_KEY = null;
        }
    }

    public static String generateHMAC(String message) {
        try{
            if (HMAC_SECRET_KEY == null){
                System.out.println("Key is null, cannot generate HMAC");
                return null;
            }

            //Creating HMAC calculator
            Mac hmac = Mac.getInstance(ALGORITHM);

            //setting secret key
            SecretKeySpec secretKey = new SecretKeySpec(
                    HMAC_SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    0,
                    HMAC_SECRET_KEY.getBytes(StandardCharsets.UTF_8).length,
                    ALGORITHM
                    );

            //initailising HMAC with secret key
            hmac.init(secretKey);

            //Calculate the HMAC of the message
            byte[] hmacBytes = hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            //Convert to Base64 for transmission
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch(Exception e){
            System.out.println("Failed to generate HMAC: " +  e.getMessage());
            return null;
        }
    }


    public static boolean verifyHMAC(String message, String receivedHMAC) {
        try{
            //Calculating what the HMAC should be
            String computeHMAC = generateHMAC(message);

            if(computeHMAC == null || receivedHMAC == null){
                System.out.println("Failed to verify HMAC: null values");
                return false;
            }
            //comparing two HMACs
            if(computeHMAC.equals(receivedHMAC)){
                System.out.println("Successfully verified HMAC");
                return true;
            } else {
                System.out.println("Failed to verify HMAC: (Possible tampering)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to verify HMAC: " +  e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        /**
         * for testing and to show jordan how it is working
         */
        System.out.println("=== HMAC Utility Test ===\n");

        // Test 1: Generate HMAC
        System.out.println("Test 1: Generate HMAC signature");
        String testMessage = "Hello, this is a secure message";
        String hmac = generateHMAC(testMessage);
        System.out.println("Original message: " + testMessage);
        System.out.println("Generated HMAC: " + hmac);
        System.out.println();

        // Test 2: Verify correct message (should pass)
        System.out.println("Test 2: Verify original message (should PASS)");
        boolean isValid = verifyHMAC(testMessage, hmac);
        System.out.println("Result: " + (isValid ? "✓ PASS" : "✗ FAIL"));
        System.out.println();

        // Test 3: Verify tampered message (should fail)
        System.out.println("Test 3: Verify tampered message (should FAIL)");
        String tamperedMessage = "Hello, this is a TAMPERED message";
        boolean isTampered = verifyHMAC(tamperedMessage, hmac);
        System.out.println("Result: " + (isTampered ? "✗ FAIL - tampering NOT detected!" : "✓ PASS - tampering detected!"));
    }
}
