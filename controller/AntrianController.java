package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntrianController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/klinik";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; // sesuaikan jika ada password

    public static void tambahAntrian(String idPasien) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO janji_temu (id_pasien, tanggal_janji) VALUES (?, CURDATE())"
        );
        stmt.setString(1, idPasien);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public static List<String[]> getDaftarAntrian() throws SQLException {
        List<String[]> daftar = new ArrayList<>();
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id_janji, id_pasien, tanggal_janji FROM janji_temu ORDER BY id_janji ASC");

        while (rs.next()) {
            String[] data = new String[3];
            data[0] = rs.getString("id_janji");
            data[1] = rs.getString("id_pasien");
            data[2] = rs.getString("tanggal_janji");
            daftar.add(data);
        }

        rs.close();
        stmt.close();
        conn.close();
        return daftar;
    }

    public static void hapusAntrianDanCatat(String idJanji, String idPasien) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        // Catat ke statistik/rekam medis dengan data kosong (placeholder)
        PreparedStatement insertStatistik = conn.prepareStatement(
            "INSERT INTO rekam_medis (tanggal, id_pasien, keluhan, diagnosa, tindakan) VALUES (CURDATE(), ?, '', '', '')"
        );
        insertStatistik.setString(1, idPasien);
        insertStatistik.executeUpdate();

        // Hapus dari janji_temu
        PreparedStatement hapus = conn.prepareStatement(
            "DELETE FROM janji_temu WHERE id_janji = ?"
        );
        hapus.setString(1, idJanji);
        hapus.executeUpdate();

        insertStatistik.close();
        hapus.close();
        conn.close();
    }
}