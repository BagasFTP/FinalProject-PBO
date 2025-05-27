package controller;

import config.koneksi;
import model.JanjiModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JanjiController {
    public static void editJanji(JanjiModel janji) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = koneksi.getKoneksi();
            String sql = "UPDATE janji_temu SET tanggal_janji = ? WHERE id_pasien = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(janji.getTanggalJanji().getTime()));
            stmt.setString(2, janji.getIdPasien());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void hapusJanji(String idPasien) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = koneksi.getKoneksi();
            String sql = "DELETE FROM janji_temu WHERE id_pasien = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idPasien);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
