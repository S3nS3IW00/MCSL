package app.mcsl.managers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashManager {

    public static String getHash(String password) {
        MessageDigest md;
        String digest = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return digest;
    }

    public static String cuttedHash(String hash) {
        return getHash(hash).substring(0, Math.min(getHash(hash).length(), 20));
    }

}
