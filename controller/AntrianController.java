package controller;

import config.koneksi;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class AntrianController {

    public static List<String[]> getDaftarAntrian() throws Exception {
        List<String[]> daftarAntrian = new ArrayList<>();
        String sql = """
            SELECT
                a.id_antrian,
                a.id_pasien,
                p.nama_pasien,
                a.tanggal_antrian,
                a.status
            FROM antrian a
            JOIN pasien p ON a.id_pasien = p.id_pasien
            WHERE a.status IN ('waiting', 'called')
            ORDER BY a.tanggal_antrian ASC, a.nomor_antrian ASC
        """;

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("id_antrian"));
                row[1] = rs.getString("id_pasien");
                row[2] = rs.getString("nama_pasien");
                row[3] = rs.getString("tanggal_antrian");
                row[4] = rs.getString("status");
                daftarAntrian.add(row);
            }
        }
        return daftarAntrian;
    }

    public static void tambahAntrian(String idPasien, Date tanggalAntrian, int nomorAntrian) throws SQLException {
        String sql = "INSERT INTO antrian (id_pasien, tanggal_antrian, nomor_antrian, status) VALUES (?, ?, ?, 'waiting')";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPasien);
            ps.setDate(2, new java.sql.Date(tanggalAntrian.getTime()));
            ps.setInt(3, nomorAntrian);
            ps.executeUpdate();
        }
    }

    public static void panggilAntrian(int idAntrian) throws SQLException {
        String sql = "UPDATE antrian SET status = 'called' WHERE id_antrian = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAntrian);
            ps.executeUpdate();
        }
    }

    public static void selesaikanAntrian(int idAntrian) throws SQLException {
        // Call the stored procedure with id_antrian
        String sql = "{CALL SelesaikanAntrian(?)}";
        try (Connection conn = koneksi.getKoneksi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idAntrian);
            cs.execute();
        }
    }

    public static int getNextNomorAntrian(Date tanggalAntrian) throws SQLException {
        String sql = "SELECT MAX(nomor_antrian) FROM antrian WHERE tanggal_antrian = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(tanggalAntrian.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) + 1;
                }
            }
        }
        return 1;
    }

    public static String[] getAntrianDetails(int idAntrian) throws SQLException {
        String[] details = new String[2];
        String sql = "SELECT a.id_pasien, p.nama_pasien FROM antrian a JOIN pasien p ON a.id_pasien = p.id_pasien WHERE a.id_antrian = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAntrian);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    details[0] = rs.getString("id_pasien");
                    details[1] = rs.getString("nama_pasien");
                }
            }
        }
        return details;
    }
}