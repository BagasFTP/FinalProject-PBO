package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import config.koneksi;

public class PasienController {
    public static boolean cekIdPasienExist(String idPasien) {
        try {
            Connection conn = koneksi.getKoneksi();
            String sql = "SELECT COUNT(*) FROM pasien WHERE id_pasien = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idPasien);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
