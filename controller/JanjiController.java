package controller;

import config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.ResultSet; // Added for retrieving patient name

public class JanjiController {

    public static void simpanJanji(String idPasien, String tanggalJanji, String waktuJanji) throws Exception {
        Connection conn = koneksi.getKoneksi();
        String query = "{CALL BuatJanjiTemu(?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(query)) {
            cs.setString(1, idPasien);
            cs.setString(2, tanggalJanji);
            cs.setString(3, waktuJanji);
            cs.execute();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void updateStatusJanjiTemu(int idJanjiTemu, String statusBaru) throws SQLException {
        String sql = "{CALL UpdateStatusJanjiTemu(?, ?)}";
        try (Connection conn = koneksi.getKoneksi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idJanjiTemu);
            cs.setString(2, statusBaru);
            cs.execute();
        }
    }

    /**
     * Menyelesaikan janji temu dan mencatatnya ke rekam medis.
     * @param idJanjiTemu ID Janji Temu yang akan diselesaikan.
     * @param diagnosis Diagnosis yang diberikan.
     * @param tindakan Tindakan yang dilakukan.
     * @param obat Obat yang diberikan.
     * @throws SQLException jika terjadi error database.
     */
    public static void selesaikanJanjiTemu(int idJanjiTemu, String diagnosa, String tindakan, String obat_yang_diberikan) throws SQLException {
        String sql = "{CALL SelesaikanJanjiTemu(?, ?, ?, ?)}";
        try (Connection conn = koneksi.getKoneksi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idJanjiTemu);
            cs.setString(2, diagnosa);
            cs.setString(3, tindakan);
            cs.setString(4, obat_yang_diberikan);
            cs.execute();
        }
    }

    // Metode untuk mengambil nama pasien berdasarkan ID pasien
    public static String getNamaPasien(String idPasien) throws SQLException {
        String namaPasien = null;
        String sql = "SELECT nama_pasien FROM pasien WHERE id_pasien = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    namaPasien = rs.getString("nama_pasien");
                }
            }
        }
        return namaPasien;
    }
}