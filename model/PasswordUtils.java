package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    // Fungsi untuk menghasilkan hash SHA-256 dari password
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString(); // Return hash dalam format hexadecimal
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fungsi untuk memverifikasi password
    public static boolean verifyPassword(String enteredPassword, String storedPasswordHash) {
        String hashedEnteredPassword = hashPassword(enteredPassword);
        return hashedEnteredPassword.equals(storedPasswordHash);
    }
}
