package nci.security_fundamentals.auth;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncryptor {
    private final int setceaserV = 9;//values used in encryption and decryption
    private final int mult = 3;
    private final int saltL = 8;
    private final String saltSeperator = ":"; //used to seperate salt from encrypted password

    String genSalt(){
        SecureRandom rndm = new SecureRandom();//secure random generator
        byte[] saltB = new byte[saltL];// holds 8 random bytes
        rndm.nextBytes(saltB);// fill
        return Base64.getEncoder().encodeToString(saltB).substring(0, saltL);// convert to string and trim
        //base64 to encode in binary to string format
    }
    public String encryptString(String password){
        String salt = genSalt();
        StringBuilder sb = new StringBuilder(salt).append(password);
        for (int i = 0; i < sb.length(); i++) {//simple ceaser cipher
            char ceaser = sb.charAt(i);
            ceaser = (char) (ceaser + setceaserV); //cast to char and add
            sb.setCharAt(i, ceaser);
        }

        for (int i = 0; i < sb.length(); i++) {//multiply with postition
            char multiply = sb.charAt(i);
            multiply = (char) (multiply * mult + (i * 5)); //cast to char and add
            sb.setCharAt(i, multiply);
        }

        for (int i = 0; i < sb.length() - 1; i += 2) {//simple swap characters in pairs
            char swap = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i + 1));
            sb.setCharAt(i + 1, swap);
        }
        password = salt + saltSeperator + sb.toString();
        return password;
    }

    public String decryptString(String encryptedPassword){

        int separatorI = encryptedPassword.indexOf(saltSeperator);
        if (separatorI == -1){
            return encryptedPassword;
        }

        String salt = encryptedPassword.substring(0, separatorI);// rerieve salt and content. Separator
        StringBuilder sb = new StringBuilder(encryptedPassword.substring(separatorI + 1));
        for (int i = 0; i < sb.length() - 1; i += 2) {// reverse cahr swap
            char swap = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i + 1));
            sb.setCharAt(i + 1, swap);
        }

        for (int i = 0; i < sb.length(); i++) {
            char multiply = sb.charAt(i);
            multiply = (char) ((multiply - (i * 5)) / mult); //subtract pos offset and divide
            sb.setCharAt(i, multiply);
        }

        for (int i = 0; i < sb.length(); i++) {
            char ceaser = sb.charAt(i);
            ceaser = (char) (ceaser - setceaserV); //swap chars back
            sb.setCharAt(i, ceaser);
        }

        encryptedPassword = sb.substring(salt.length());
        return encryptedPassword;
    }
}
