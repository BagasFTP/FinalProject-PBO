package controller;

import config.koneksi; // Pastikan class koneksi ada dan berfungsi dengan benar
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntrianController {

    /**
     * Mengambil daftar antrian yang sedang aktif.
     * @return List<String[]> berisi [id_janji, id_pasien, nama_pasien, tanggal_antrian, status]
     * @throws Exception jika terjadi kesalahan database
     */
    public static List<String[]> getDaftarAntrian() throws Exception {
        List<String[]> daftarAntrian = new ArrayList<>();
        // Query untuk mengambil data antrian aktif beserta nama pasien
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

        try (Connection conn = koneksi.getKoneksi(); // Mendapatkan koneksi dari class koneksi
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
     * Menambah antrian baru untuk pasien.
     * @param idPasien ID pasien yang akan ditambahkan ke antrian
     * @throws Exception jika terjadi kesalahan atau validasi gagal
     */
    public static void tambahAntrian(String idPasien) throws Exception {
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false); // Memulai transaksi

            // Validasi apakah pasien ada di database
            if (!cekPasienAda(idPasien, conn)) { // Menggunakan koneksi yang sama untuk efisiensi
                throw new Exception("ID Pasien " + idPasien + " tidak ditemukan!");
            }

            LocalDate tanggalHariIni = LocalDate.now();

            // Cek apakah pasien sudah ada dalam antrian hari ini
            if (cekPasienSudahAntri(idPasien, tanggalHariIni, conn)) { // Menggunakan koneksi yang sama
                throw new Exception("Pasien sudah ada dalam antrian hari ini!");
            }

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

            conn.commit(); // Commit transaksi jika semua berhasil
            System.out.println("Antrian berhasil ditambahkan untuk pasien: " + idPasien);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaksi jika terjadi error SQL
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam tambahAntrian (SQLException): " + e.getMessage());
            throw new Exception("Gagal menambahkan antrian (SQLException): " + e.getMessage(), e);
        } catch (Exception e) { // Menangkap exception custom juga
             if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam tambahAntrian: " + e.getMessage());
            throw e; // Melempar kembali exception custom
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan ke mode auto-commit
                    conn.close(); // Tutup koneksi
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Menyelesaikan antrian (menghapus dari antrian aktif) dan mencatat ke history.
     * @param idPasien ID pasien yang antriannya akan diselesaikan
     * @param idJanji ID janji terkait antrian tersebut
     * @throws Exception jika terjadi kesalahan database
     */
    public static void hapusAntrianDanCatat(String idPasien, String idJanji) throws Exception {
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false); // Memulai transaksi

            // 1. Ambil data antrian sebelum dihapus untuk dipindahkan
            String sqlSelect = """
                SELECT tanggal_antrian, waktu_masuk
                FROM antrian
                WHERE id_pasien = ? AND id_janji = ? AND status IN ('waiting', 'called')
            """;
            Date tanggalKunjunganDb = null;
            Timestamp waktuMasukDb = null;

            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setString(1, idPasien);
                psSelect.setInt(2, Integer.parseInt(idJanji)); // id_janji adalah INT
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        tanggalKunjunganDb = rs.getDate("tanggal_antrian");
                        waktuMasukDb = rs.getTimestamp("waktu_masuk");
                    } else {
                        throw new Exception("Data antrian aktif tidak ditemukan untuk pasien " + idPasien + " dan janji " + idJanji);
                    }
                }
            }

            // 2. Pindahkan ke tabel antrian_selesai
            String sqlInsertSelesai = """
                INSERT INTO antrian_selesai (id_pasien, id_janji, tanggal_kunjungan, waktu_mulai, waktu_selesai, keterangan)
                VALUES (?, ?, ?, ?, NOW(), 'Antrian selesai')
            """;
            try (PreparedStatement psInsertSelesai = conn.prepareStatement(sqlInsertSelesai)) {
                psInsertSelesai.setString(1, idPasien);
                psInsertSelesai.setInt(2, Integer.parseInt(idJanji));
                psInsertSelesai.setDate(3, tanggalKunjunganDb);
                psInsertSelesai.setTimestamp(4, waktuMasukDb); // waktu_masuk dari tabel antrian
                psInsertSelesai.executeUpdate();
            }

            // 3. Hapus dari antrian aktif (atau update status menjadi 'completed')
            // Sesuai logika awal, kita hapus. Alternatifnya bisa update status.
            String sqlDelete = "DELETE FROM antrian WHERE id_pasien = ? AND id_janji = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setString(1, idPasien);
                psDelete.setInt(2, Integer.parseInt(idJanji));
                int result = psDelete.executeUpdate();
                if (result <= 0) {
                    // Ini bisa terjadi jika antrian sudah tidak ada (misal, sudah diproses oleh request lain)
                    // Sebaiknya tidak throw error fatal, tapi bisa dicatat/log.
                    System.out.println("Peringatan: Antrian untuk pasien " + idPasien + " dan janji " + idJanji + " tidak ditemukan saat akan dihapus (mungkin sudah diproses).");
                }
            }

            // 4. Update status janji menjadi 'completed'
            String sqlUpdateJanji = "UPDATE janji SET status = 'completed' WHERE id_janji = ?";
            try (PreparedStatement psUpdateJanji = conn.prepareStatement(sqlUpdateJanji)) {
                psUpdateJanji.setInt(1, Integer.parseInt(idJanji));
                psUpdateJanji.executeUpdate();
            }

            // 5. Tambahkan ke statistik kunjungan
            // Menggunakan tanggalKunjunganDb (dari antrian.tanggal_antrian)
            tambahKeStatistikKunjungan(conn, idPasien, tanggalKunjunganDb);

            conn.commit(); // Commit transaksi
            System.out.println("Antrian berhasil diselesaikan dan dicatat untuk pasien: " + idPasien);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam hapusAntrianDanCatat (SQLException): " + e.getMessage());
            throw new Exception("Gagal menyelesaikan antrian (SQLException): " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error dalam hapusAntrianDanCatat: " + e.getMessage());
            throw e;
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
     * Mengecek apakah pasien ada di database.
     * Disarankan menggunakan koneksi yang sudah ada jika dalam transaksi.
     * @param idPasien ID pasien yang akan dicek
     * @param existingConn Koneksi database yang sudah ada (opsional, bisa null)
     * @return true jika pasien ada, false jika tidak
     * @throws SQLException jika terjadi error database
     */
    public static boolean cekPasienAda(String idPasien, Connection existingConn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pasien WHERE id_pasien = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = (existingConn != null) ? existingConn : koneksi.getKoneksi();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idPasien);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            if (rs != null && (existingConn == null || conn != existingConn)) rs.close(); // Hanya tutup jika rs dibuat di sini
            if (ps != null && (existingConn == null || conn != existingConn)) ps.close(); // Hanya tutup jika ps dibuat di sini
            if (conn != null && existingConn == null) { // Hanya tutup koneksi jika dibuat di method ini
                conn.close();
            }
        }
    }
     /**
     * Overload cekPasienAda jika tidak ada koneksi yang di-pass.
     * Akan membuat koneksi baru.
     */
    public static boolean cekPasienAda(String idPasien) throws Exception {
        return cekPasienAda(idPasien, null);
    }


    /**
     * Mengecek apakah pasien sudah ada dalam antrian pada tanggal tertentu.
     * @param idPasien ID pasien
     * @param tanggal Tanggal antrian
     * @param existingConn Koneksi database yang sudah ada (opsional, bisa null)
     * @return true jika sudah antri, false jika belum
     * @throws SQLException jika terjadi error database
     */
    private static boolean cekPasienSudahAntri(String idPasien, LocalDate tanggal, Connection existingConn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM antrian WHERE id_pasien = ? AND tanggal_antrian = ? AND status IN ('waiting', 'called')";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = (existingConn != null) ? existingConn : koneksi.getKoneksi();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idPasien);
            ps.setDate(2, Date.valueOf(tanggal));
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
             if (rs != null && (existingConn == null || conn != existingConn)) rs.close();
             if (ps != null && (existingConn == null || conn != existingConn)) ps.close();
            if (conn != null && existingConn == null) {
                conn.close();
            }
        }
    }

    /**
     * Mencari janji yang ada atau membuat janji baru jika tidak ada.
     * Harus dijalankan dalam konteks transaksi yang sudah ada (conn tidak boleh null).
     * @param conn Koneksi database yang aktif (tidak boleh null)
     * @param idPasien ID pasien
     * @param tanggal Tanggal janji
     * @return ID janji yang ditemukan atau baru dibuat
     * @throws SQLException jika terjadi error database atau gagal membuat janji
     */
    private static int cariAtauBuatJanji(Connection conn, String idPasien, LocalDate tanggal) throws SQLException {
        // Cari janji yang sudah ada untuk pasien tersebut pada tanggal tersebut dengan status 'active'
        String sqlCari = "SELECT id_janji FROM janji WHERE id_pasien = ? AND tanggal_janji = ? AND status = 'active'";
        try (PreparedStatement psCari = conn.prepareStatement(sqlCari)) {
            psCari.setString(1, idPasien);
            psCari.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = psCari.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_janji"); // Janji ditemukan
                }
            }
        }

        // Jika tidak ada janji aktif, buat janji baru
        String sqlBuat = "INSERT INTO janji (id_pasien, tanggal_janji, status) VALUES (?, ?, 'active')";
        try (PreparedStatement psBuat = conn.prepareStatement(sqlBuat, Statement.RETURN_GENERATED_KEYS)) {
            psBuat.setString(1, idPasien);
            psBuat.setDate(2, Date.valueOf(tanggal));
            int result = psBuat.executeUpdate();
            if (result > 0) {
                try (ResultSet rsKeys = psBuat.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        return rsKeys.getInt(1); // ID janji baru berhasil dibuat
                    }
                }
            }
        }
        throw new SQLException("Gagal mencari atau membuat janji baru untuk pasien " + idPasien);
    }

    /**
     * Mendapatkan nomor antrian berikutnya untuk tanggal tertentu.
     * Harus dijalankan dalam konteks transaksi yang sudah ada (conn tidak boleh null).
     * @param conn Koneksi database yang aktif (tidak boleh null)
     * @param tanggal Tanggal antrian
     * @return Nomor antrian berikutnya
     * @throws SQLException jika terjadi error database
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
        return 1; // Default jika tidak ada antrian sama sekali pada tanggal tersebut
    }

    /**
     * Menambahkan data ke statistik kunjungan.
     * Disesuaikan agar konsisten dengan stored procedure SelesaikanAntrian di klinik.sql.
     * Stored procedure tersebut tidak memasukkan 'biaya' atau 'status_pembayaran'.
     * View 'view_statistik_bulanan' di klinik.sql mereferensikan 'biaya', yang mengindikasikan
     * potensi inkonsistensi dalam skema SQL itu sendiri. Metode ini akan mengikuti logika prosedur.
     * @param conn Koneksi database yang aktif (tidak boleh null)
     * @param idPasien ID pasien
     * @param tanggalKunjungan Tanggal kunjungan (berasal dari antrian.tanggal_antrian)
     * @throws SQLException jika terjadi error database
     */
    private static void tambahKeStatistikKunjungan(Connection conn, String idPasien, Date tanggalKunjungan) throws SQLException {
        String sql = """
            INSERT INTO statistik_kunjungan (id_pasien, tanggal_kunjungan, jenis_kunjungan)
            VALUES (?, ?, 'konsultasi')
        """;
        // Catatan: Prosedur SelesaikanAntrian di klinik.sql menggunakan:
        // ON DUPLICATE KEY UPDATE tanggal_kunjungan = tanggal_kunjungan;
        // Klausa ON DUPLICATE KEY UPDATE tersebut mungkin tidak berfungsi seperti yang diharapkan
        // jika tidak ada UNIQUE KEY yang relevan pada (id_pasien, tanggal_kunjungan).
        // Dengan id_kunjungan sebagai PK auto-increment, setiap INSERT akan membuat baris baru.
        // Untuk kesederhanaan dan asumsi bahwa setiap antrian selesai adalah entri statistik unik,
        // klausa ON DUPLICATE KEY dihilangkan di sini. Jika diperlukan, tambahkan kembali
        // dengan pemahaman yang jelas tentang UNIQUE KEY yang ditargetkan.

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPasien);
            ps.setDate(2, tanggalKunjungan); // Menggunakan tanggal dari antrian
            ps.executeUpdate();
        }
    }
}
