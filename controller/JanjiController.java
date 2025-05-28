package controller;

import config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class JanjiController {
    public static void simpanJanji(String idPasien, String tanggalJanji) throws Exception {
        Connection conn = koneksi.getKoneksi();
        String query = "INSERT INTO janji_temu (id_pasien, tanggal_janji, status) VALUES (?, ?, 'Menunggu')";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, idPasien);
        ps.setString(2, tanggalJanji);
        ps.executeUpdate();
        ps.close();
    }
}
