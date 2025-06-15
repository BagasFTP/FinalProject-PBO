package model;

import java.sql.*;
import java.security.MessageDigest;
import config.koneksi;

public class AdminLogin {

    // Fungsi untuk melakukan hashing password menggunakan SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fungsi untuk memverifikasi login admin
    public static boolean verifyAdminLogin(String username, String password) {
        String hashedPassword = hashPassword(password); // Hash password yang dimasukkan
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;  // Login berhasil
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;  // Login gagal
    }
}
