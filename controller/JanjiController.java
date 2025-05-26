package controller;

import model.JanjiModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import config.koneksi;

public class JanjiController {

    public static void simpanJanji(JanjiModel janji) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = koneksi.getKoneksi(); 
            String sql = "INSERT INTO janji_temu (id_pasien, tanggal_janji) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, janji.getIdPasien());

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(janji.getTanggalJanji().getTime());
            stmt.setDate(2, sqlDate);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException("Gagal menyimpan janji: " + ex.getMessage());
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
