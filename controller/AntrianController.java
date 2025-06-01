package controller;

import config.koneksi;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntrianController {
    
    /**
     * Mengambil daftar antrian yang sedang aktif
     * @return List<String[]> berisi [id_janji, id_pasien, nama_pasien, tanggal_antrian, status]
     */
    public static List<String[]> getDaftarAntrian() throws Exception {
        List<String[]> daftarAntrian = new ArrayList<>();
        
        String sql = """
            SELECT 
                a.id_janji,
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
                row[0] = rs.getString("id_janji");
                row[1] = rs.getString("id_pasien"); 
                row[2] = rs.getString("nama_pasien");
                row[3] = rs.getString("tanggal_antrian");
                row[4] = rs.getString("status");
                
                daftarAntrian.add(row);
            }
            
        } catch (SQLException e) {
            System.err.println("Error dalam getDaftarAntrian: " + e.getMessage());
            throw new Exception("Gagal mengambil data antrian: " + e.getMessage(), e);
        }
        
        return daftarAntrian;
    }
    
    /**
     * Menambah antrian baru untuk pasien
     * @param idPasien ID pasien yang akan ditambahkan ke antrian
     */
    public static void tambahAntrian(String idPasien) throws Exception {
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false); // Start transaction
            
            // Validasi pasien ada
            if (!cekPasienAda(idPasien)) {
                throw new Exception("ID Pasien " + idPasien + " tidak ditemukan!");
            }
            
            // Cek apakah pasien sudah ada dalam antrian hari ini
            if (cekPasienSudahAntri(idPasien, LocalDate.now())) {
                throw new Exception("Pasien sudah ada dalam antrian hari ini!");
            }
            
            LocalDate tanggalHariIni = LocalDate.now();
            
            // Cari atau buat janji untuk hari ini
            int idJanji = cariAtauBuatJanji(conn, idPasien, tanggalHariIni);
            
            // Dapatkan nomor antrian berikutnya
            int nomorAntrian = getNomorAntrianBerikutnya(conn, tanggalHariIni);
            
            // Insert ke tabel antrian
            String sqlInsert = """
                INSERT INTO antrian (id_janji, id_pasien, tanggal_antrian, nomor_antrian, status) 
                VALUES (?, ?, ?, ?, 'waiting')
            """;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, idJanji);
                ps.setString(2, idPasien);
                ps.setDate(3, Date.valueOf(tanggalHariIni));
                ps.setInt(4, nomorAntrian);
                
                int result = ps.executeUpdate();
                if (result <= 0) {
                    throw new Exception("Gagal menambahkan antrian ke database");
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("Antrian berhasil ditambahkan untuk pasien: " + idPasien);
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam tambahAntrian: " + e.getMessage());
            throw new Exception("Gagal menambahkan antrian: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Menyelesaikan antrian dan mencatat ke history
     * @param idPasien ID pasien yang akan diselesaikan
     * @param idJanji ID janji terkait
     */
    public static void hapusAntrianDanCatat(String idPasien, String idJanji) throws Exception {
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false);
            
            // Ambil data antrian sebelum dihapus
            String sqlSelect = """
                SELECT id_janji, id_pasien, tanggal_antrian, waktu_masuk 
                FROM antrian 
                WHERE id_pasien = ? AND id_janji = ?
            """;
            
            String tanggalKunjungan = null;
            Timestamp waktuMasuk = null;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
                ps.setString(1, idPasien);
                ps.setInt(2, Integer.parseInt(idJanji));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tanggalKunjungan = rs.getString("tanggal_antrian");
                        waktuMasuk = rs.getTimestamp("waktu_masuk");
                    } else {
                        throw new Exception("Data antrian tidak ditemukan!");
                    }
                }
            }
            
            // Pindahkan ke tabel antrian_selesai
            String sqlInsertSelesai = """
                INSERT INTO antrian_selesai (id_pasien, id_janji, tanggal_kunjungan, waktu_mulai, waktu_selesai, keterangan) 
                VALUES (?, ?, ?, ?, NOW(), 'Antrian selesai')
            """;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertSelesai)) {
                ps.setString(1, idPasien);
                ps.setInt(2, Integer.parseInt(idJanji));
                ps.setString(3, tanggalKunjungan);
                ps.setTimestamp(4, waktuMasuk);
                ps.executeUpdate();
            }
            
            // Hapus dari antrian aktif
            String sqlDelete = "DELETE FROM antrian WHERE id_pasien = ? AND id_janji = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setString(1, idPasien);
                ps.setInt(2, Integer.parseInt(idJanji));
                
                int result = ps.executeUpdate();
                if (result <= 0) {
                    throw new Exception("Gagal menghapus antrian dari database");
                }
            }
            
            // Update status janji menjadi completed
            String sqlUpdateJanji = "UPDATE janji SET status = 'completed' WHERE id_janji = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateJanji)) {
                ps.setInt(1, Integer.parseInt(idJanji));
                ps.executeUpdate();
            }
            
            // Tambahkan ke statistik kunjungan
            tambahKeStatistikKunjungan(conn, idPasien, Date.valueOf(tanggalKunjungan));
            
            conn.commit();
            System.out.println("Antrian berhasil diselesaikan untuk pasien: " + idPasien);
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam hapusAntrianDanCatat: " + e.getMessage());
            throw new Exception("Gagal menyelesaikan antrian: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Mengecek apakah pasien ada di database
     * @param idPasien ID pasien yang akan dicek
     * @return true jika pasien ada, false jika tidak
     */
    public static boolean cekPasienAda(String idPasien) {
        String sql = "SELECT COUNT(*) FROM pasien WHERE id_pasien = ?";
        
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error dalam cekPasienAda: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mengecek apakah pasien sudah ada dalam antrian pada tanggal tertentu
     */
    private static boolean cekPasienSudahAntri(String idPasien, LocalDate tanggal) throws SQLException {
        String sql = "SELECT COUNT(*) FROM antrian WHERE id_pasien = ? AND tanggal_antrian = ? AND status IN ('waiting', 'called')";
        
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idPasien);
            ps.setDate(2, Date.valueOf(tanggal));
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * Mencari janji yang ada atau membuat janji baru jika tidak ada
     */
    private static int cariAtauBuatJanji(Connection conn, String idPasien, LocalDate tanggal) throws SQLException {
        // Cari janji yang sudah ada
        String sqlCari = "SELECT id_janji FROM janji WHERE id_pasien = ? AND tanggal_janji = ? AND status = 'active'";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlCari)) {
            ps.setString(1, idPasien);
            ps.setDate(2, Date.valueOf(tanggal));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_janji");
                }
            }
        }
        
        // Jika tidak ada, buat janji baru
        String sqlBuat = "INSERT INTO janji (id_pasien, tanggal_janji, status) VALUES (?, ?, 'active')";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlBuat, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, idPasien);
            ps.setDate(2, Date.valueOf(tanggal));
            
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        
        throw new SQLException("Gagal membuat janji baru");
    }
    
    /**
     * Mendapatkan nomor antrian berikutnya untuk tanggal tertentu
     */
    private static int getNomorAntrianBerikutnya(Connection conn, LocalDate tanggal) throws SQLException {
        String sql = "SELECT COALESCE(MAX(nomor_antrian), 0) + 1 FROM antrian WHERE tanggal_antrian = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 1; // Default jika tidak ada data
    }
    
    /**
     * Menambahkan data ke statistik kunjungan
     */
    private static void tambahKeStatistikKunjungan(Connection conn, String idPasien, Date tanggalKunjungan) throws SQLException {
        String sql = """
            INSERT INTO statistik_kunjungan (id_pasien, tanggal_kunjungan, jenis_kunjungan, biaya, status_pembayaran) 
            VALUES (?, ?, 'konsultasi', 0, 'belum_lunas')
            ON DUPLICATE KEY UPDATE tanggal_kunjungan = VALUES(tanggal_kunjungan)
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPasien);
            ps.setDate(2, tanggalKunjungan);
            ps.executeUpdate();
        }
    }
}