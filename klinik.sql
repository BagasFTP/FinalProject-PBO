-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 01 Jun 2025 pada 20.12
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `klinik`
--

DELIMITER $$
--
-- Prosedur
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `BuatJanjiTemu` (IN `p_id_pasien` VARCHAR(20), IN `p_tanggal_janji` DATE, IN `p_waktu_janji` TIME)   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Insert ke tabel janji
    INSERT INTO janji (id_pasien, tanggal_janji, status) 
    VALUES (p_id_pasien, p_tanggal_janji, 'active');
    
    -- Insert ke tabel janji_temu
    INSERT INTO janji_temu (id_pasien, tanggal_janji, waktu_janji, status, keterangan) 
    VALUES (p_id_pasien, p_tanggal_janji, p_waktu_janji, 'scheduled', 'Janji temu reguler');
    
    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SelesaikanAntrian` (IN `p_id_pasien` VARCHAR(20), IN `p_id_janji` INT)   BEGIN
    -- Pindahkan ke tabel antrian_selesai
    INSERT INTO antrian_selesai (id_pasien, id_janji, tanggal_kunjungan, waktu_mulai, waktu_selesai)
    SELECT id_pasien, id_janji, tanggal_antrian, waktu_masuk, NOW()
    FROM antrian 
    WHERE id_pasien = p_id_pasien AND id_janji = p_id_janji;
    
    -- Hapus dari antrian aktif
    DELETE FROM antrian 
    WHERE id_pasien = p_id_pasien AND id_janji = p_id_janji;
    
    -- Update status janji menjadi completed
    UPDATE janji SET status = 'completed' 
    WHERE id_janji = p_id_janji;
    
    -- Tambahkan ke statistik kunjungan
    INSERT INTO statistik_kunjungan (id_pasien, tanggal_kunjungan, jenis_kunjungan)
    VALUES (p_id_pasien, CURDATE(), 'konsultasi')
    ON DUPLICATE KEY UPDATE tanggal_kunjungan = tanggal_kunjungan;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `TambahAntrian` (IN `p_id_pasien` VARCHAR(20), IN `p_tanggal` DATE)   BEGIN
    DECLARE v_id_janji INT;
    DECLARE v_nomor_antrian INT;
    
    -- Cari janji aktif untuk pasien di tanggal tersebut
    SELECT id_janji INTO v_id_janji 
    FROM janji 
    WHERE id_pasien = p_id_pasien 
    AND tanggal_janji = p_tanggal 
    AND status = 'active' 
    LIMIT 1;
    
    -- Jika tidak ada janji, buat janji baru
    IF v_id_janji IS NULL THEN
        INSERT INTO janji (id_pasien, tanggal_janji, status) 
        VALUES (p_id_pasien, p_tanggal, 'active');
        SET v_id_janji = LAST_INSERT_ID();
    END IF;
    
    -- Dapatkan nomor antrian berikutnya untuk tanggal tersebut
    SELECT COALESCE(MAX(nomor_antrian), 0) + 1 INTO v_nomor_antrian
    FROM antrian 
    WHERE tanggal_antrian = p_tanggal;
    
    -- Insert antrian baru
    INSERT INTO antrian (id_janji, id_pasien, tanggal_antrian, nomor_antrian, status)
    VALUES (v_id_janji, p_id_pasien, p_tanggal, v_nomor_antrian, 'waiting');
    
END$$

--
-- Fungsi
--
CREATE DEFINER=`root`@`localhost` FUNCTION `CekKonflikJadwal` (`p_tanggal` DATE, `p_waktu` TIME, `p_id_pasien` VARCHAR(20)) RETURNS TINYINT(1) DETERMINISTIC READS SQL DATA BEGIN
    DECLARE konflik_count INT DEFAULT 0;
    
    -- Cek apakah ada janji di waktu yang sama (dalam rentang 30 menit)
    SELECT COUNT(*) INTO konflik_count
    FROM janji_temu 
    WHERE tanggal_janji = p_tanggal
    AND status = 'scheduled'
    AND (
        -- Konflik jika ada janji dalam rentang 30 menit
        (waktu_janji BETWEEN SUBTIME(p_waktu, '00:30:00') AND ADDTIME(p_waktu, '00:30:00'))
        OR 
        -- Konflik jika pasien yang sama sudah ada janji di hari yang sama
        (id_pasien = p_id_pasien)
    );
    
    RETURN konflik_count > 0;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `antrian`
--

CREATE TABLE `antrian` (
  `id_antrian` int(11) NOT NULL,
  `id_janji` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal_antrian` date NOT NULL,
  `nomor_antrian` int(11) DEFAULT NULL,
  `status` enum('waiting','called','completed','cancelled') DEFAULT 'waiting',
  `waktu_masuk` timestamp NOT NULL DEFAULT current_timestamp(),
  `waktu_selesai` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `antrian_selesai`
--

CREATE TABLE `antrian_selesai` (
  `id_selesai` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `id_janji` int(11) DEFAULT NULL,
  `tanggal_kunjungan` date NOT NULL,
  `waktu_mulai` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `waktu_selesai` timestamp NOT NULL DEFAULT current_timestamp(),
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `antrian_selesai`
--

INSERT INTO `antrian_selesai` (`id_selesai`, `id_pasien`, `id_janji`, `tanggal_kunjungan`, `waktu_mulai`, `waktu_selesai`, `keterangan`) VALUES
(15, 'P008', 10, '2025-06-02', '2025-06-01 17:07:09', '2025-06-01 18:10:41', 'Antrian selesai');

-- --------------------------------------------------------

--
-- Struktur dari tabel `janji`
--

CREATE TABLE `janji` (
  `id_janji` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal_janji` date NOT NULL,
  `status` enum('active','completed','cancelled') DEFAULT 'active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `janji`
--

INSERT INTO `janji` (`id_janji`, `id_pasien`, `tanggal_janji`, `status`, `created_at`, `updated_at`) VALUES
(10, 'P008', '2025-06-02', 'completed', '2025-06-01 17:07:09', '2025-06-01 18:10:41'),
(17, 'P008', '2025-06-02', 'active', '2025-06-01 17:12:41', '2025-06-01 17:12:41');

-- --------------------------------------------------------

--
-- Struktur dari tabel `janji_temu`
--

CREATE TABLE `janji_temu` (
  `id_janji_temu` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal_janji` date NOT NULL,
  `waktu_janji` time DEFAULT NULL,
  `status` enum('scheduled','completed','cancelled') DEFAULT 'scheduled',
  `keterangan` text DEFAULT 'Janji temu reguler',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Trigger `janji_temu`
--
DELIMITER $$
CREATE TRIGGER `before_insert_janji_temu` BEFORE INSERT ON `janji_temu` FOR EACH ROW BEGIN
    -- Pastikan waktu janji tidak kosong
    IF NEW.waktu_janji IS NULL THEN
        SET NEW.waktu_janji = '09:00:00';
    END IF;
    
    -- Pastikan keterangan tidak kosong
    IF NEW.keterangan IS NULL OR NEW.keterangan = '' THEN
        SET NEW.keterangan = 'Janji temu reguler';
    END IF;
    
    -- Validasi tanggal tidak boleh di masa lalu
    IF NEW.tanggal_janji < CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tanggal janji tidak boleh di masa lalu';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pasien`
--

CREATE TABLE `pasien` (
  `id_pasien` varchar(20) NOT NULL,
  `nama_pasien` varchar(100) NOT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `no_telepon` varchar(15) DEFAULT NULL,
  `jenis_kelamin` enum('L','P') DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pasien`
--

INSERT INTO `pasien` (`id_pasien`, `nama_pasien`, `tanggal_lahir`, `alamat`, `no_telepon`, `jenis_kelamin`, `created_at`, `updated_at`) VALUES
('P001', 'Ahmad Pratama', '1990-05-15', 'Jl. Merdeka No. 123, Malang', '081234567890', 'L', '2025-06-01 13:14:08', '2025-06-01 13:14:08'),
('P002', 'Sari Dewi', '1985-08-22', 'Jl. Pahlawan No. 45, Malang', '081234567891', 'P', '2025-06-01 13:14:08', '2025-06-01 13:14:08'),
('P003', 'Budi Santoso', '1978-12-10', 'Jl. Veteran No. 67, Malang', '081234567892', 'L', '2025-06-01 13:14:08', '2025-06-01 13:14:08'),
('P004', 'Rina Maharani', '1992-03-08', 'Jl. Diponegoro No. 89, Malang', '081234567893', 'P', '2025-06-01 13:14:08', '2025-06-01 13:14:08'),
('P005', 'Dedi Kurniawan', '1988-07-30', 'Jl. Sukarno Hatta No. 12, Malang', '081234567894', 'L', '2025-06-01 13:14:08', '2025-06-01 13:14:08'),
('P006', 'Devinka Marta Legawa', '2004-03-04', 'Jl. Gajahmada No. 24, Sidoarjo', '089523483603', 'L', '2025-06-01 14:13:41', '2025-06-01 14:13:41'),
('P008', 'Chelvin Ramadani', '2004-05-16', 'Karangpilang', '0892848938393', 'L', '2025-06-01 17:06:56', '2025-06-01 17:06:56');

-- --------------------------------------------------------

--
-- Struktur dari tabel `rekam_medis`
--

CREATE TABLE `rekam_medis` (
  `id_rekam` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal` date NOT NULL,
  `keluhan` text NOT NULL,
  `diagnosa` text NOT NULL,
  `tindakan` text DEFAULT NULL,
  `obat_yang_diberikan` text DEFAULT NULL,
  `catatan_tambahan` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `statistik_kunjungan`
--

CREATE TABLE `statistik_kunjungan` (
  `id_kunjungan` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal_kunjungan` date NOT NULL,
  `jenis_kunjungan` enum('konsultasi','kontrol','emergency') DEFAULT 'konsultasi',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `statistik_kunjungan`
--

INSERT INTO `statistik_kunjungan` (`id_kunjungan`, `id_pasien`, `tanggal_kunjungan`, `jenis_kunjungan`, `created_at`) VALUES
(1, 'P008', '2025-06-02', 'konsultasi', '2025-06-01 18:10:41');

-- --------------------------------------------------------

--
-- Stand-in struktur untuk tampilan `view_antrian_aktif`
-- (Lihat di bawah untuk tampilan aktual)
--
CREATE TABLE `view_antrian_aktif` (
`id_antrian` int(11)
,`id_janji` int(11)
,`id_pasien` varchar(20)
,`nama_pasien` varchar(100)
,`tanggal_antrian` date
,`nomor_antrian` int(11)
,`status` enum('waiting','called','completed','cancelled')
,`waktu_masuk` timestamp
);

-- --------------------------------------------------------

--
-- Stand-in struktur untuk tampilan `view_janji_hari_ini`
-- (Lihat di bawah untuk tampilan aktual)
--
CREATE TABLE `view_janji_hari_ini` (
`id_janji` int(11)
,`id_pasien` varchar(20)
,`nama_pasien` varchar(100)
,`tanggal_janji` date
,`status` enum('active','completed','cancelled')
);

-- --------------------------------------------------------

--
-- Stand-in struktur untuk tampilan `view_janji_temu_lengkap`
-- (Lihat di bawah untuk tampilan aktual)
--
CREATE TABLE `view_janji_temu_lengkap` (
`id_janji_temu` int(11)
,`id_pasien` varchar(20)
,`nama_pasien` varchar(100)
,`tanggal_janji` date
,`waktu_janji` varchar(10)
,`status` enum('scheduled','completed','cancelled')
,`keterangan` text
,`created_at` timestamp
,`status_waktu` varchar(11)
);

-- --------------------------------------------------------

--
-- Stand-in struktur untuk tampilan `view_statistik_janji_temu`
-- (Lihat di bawah untuk tampilan aktual)
--
CREATE TABLE `view_statistik_janji_temu` (
`tanggal` date
,`total_janji` bigint(21)
,`terjadwal` bigint(21)
,`selesai` bigint(21)
,`dibatalkan` bigint(21)
,`jam_pertama` time
,`jam_terakhir` time
);

-- --------------------------------------------------------

--
-- Struktur untuk view `view_antrian_aktif`
--
DROP TABLE IF EXISTS `view_antrian_aktif`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_antrian_aktif`  AS SELECT `a`.`id_antrian` AS `id_antrian`, `a`.`id_janji` AS `id_janji`, `a`.`id_pasien` AS `id_pasien`, `p`.`nama_pasien` AS `nama_pasien`, `a`.`tanggal_antrian` AS `tanggal_antrian`, `a`.`nomor_antrian` AS `nomor_antrian`, `a`.`status` AS `status`, `a`.`waktu_masuk` AS `waktu_masuk` FROM (`antrian` `a` join `pasien` `p` on(`a`.`id_pasien` = `p`.`id_pasien`)) WHERE `a`.`status` in ('waiting','called') ORDER BY `a`.`tanggal_antrian` ASC, `a`.`nomor_antrian` ASC ;

-- --------------------------------------------------------

--
-- Struktur untuk view `view_janji_hari_ini`
--
DROP TABLE IF EXISTS `view_janji_hari_ini`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_janji_hari_ini`  AS SELECT `j`.`id_janji` AS `id_janji`, `j`.`id_pasien` AS `id_pasien`, `p`.`nama_pasien` AS `nama_pasien`, `j`.`tanggal_janji` AS `tanggal_janji`, `j`.`status` AS `status` FROM (`janji` `j` join `pasien` `p` on(`j`.`id_pasien` = `p`.`id_pasien`)) WHERE `j`.`tanggal_janji` = curdate() ORDER BY `j`.`id_janji` ASC ;

-- --------------------------------------------------------

--
-- Struktur untuk view `view_janji_temu_lengkap`
--
DROP TABLE IF EXISTS `view_janji_temu_lengkap`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_janji_temu_lengkap`  AS SELECT `jt`.`id_janji_temu` AS `id_janji_temu`, `jt`.`id_pasien` AS `id_pasien`, `p`.`nama_pasien` AS `nama_pasien`, `jt`.`tanggal_janji` AS `tanggal_janji`, coalesce(`jt`.`waktu_janji`,'00:00:00') AS `waktu_janji`, `jt`.`status` AS `status`, `jt`.`keterangan` AS `keterangan`, `jt`.`created_at` AS `created_at`, CASE WHEN `jt`.`tanggal_janji` = curdate() THEN 'Hari ini' WHEN `jt`.`tanggal_janji` = curdate() + interval 1 day THEN 'Besok' WHEN `jt`.`tanggal_janji` < curdate() THEN 'Sudah lewat' ELSE 'Akan datang' END AS `status_waktu` FROM (`janji_temu` `jt` join `pasien` `p` on(`jt`.`id_pasien` = `p`.`id_pasien`)) ORDER BY `jt`.`tanggal_janji` DESC, `jt`.`waktu_janji` ASC ;

-- --------------------------------------------------------

--
-- Struktur untuk view `view_statistik_janji_temu`
--
DROP TABLE IF EXISTS `view_statistik_janji_temu`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_statistik_janji_temu`  AS SELECT cast(`janji_temu`.`tanggal_janji` as date) AS `tanggal`, count(0) AS `total_janji`, count(case when `janji_temu`.`status` = 'scheduled' then 1 end) AS `terjadwal`, count(case when `janji_temu`.`status` = 'completed' then 1 end) AS `selesai`, count(case when `janji_temu`.`status` = 'cancelled' then 1 end) AS `dibatalkan`, min(`janji_temu`.`waktu_janji`) AS `jam_pertama`, max(`janji_temu`.`waktu_janji`) AS `jam_terakhir` FROM `janji_temu` GROUP BY cast(`janji_temu`.`tanggal_janji` as date) ORDER BY cast(`janji_temu`.`tanggal_janji` as date) DESC ;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `antrian`
--
ALTER TABLE `antrian`
  ADD PRIMARY KEY (`id_antrian`),
  ADD KEY `id_janji` (`id_janji`),
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `idx_tanggal` (`tanggal_antrian`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_antrian_status_tanggal` (`status`,`tanggal_antrian`);

--
-- Indeks untuk tabel `antrian_selesai`
--
ALTER TABLE `antrian_selesai`
  ADD PRIMARY KEY (`id_selesai`),
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `idx_tanggal` (`tanggal_kunjungan`);

--
-- Indeks untuk tabel `janji`
--
ALTER TABLE `janji`
  ADD PRIMARY KEY (`id_janji`),
  ADD KEY `idx_pasien_tanggal` (`id_pasien`,`tanggal_janji`),
  ADD KEY `idx_tanggal` (`tanggal_janji`),
  ADD KEY `idx_janji_status` (`status`);

--
-- Indeks untuk tabel `janji_temu`
--
ALTER TABLE `janji_temu`
  ADD PRIMARY KEY (`id_janji_temu`),
  ADD KEY `idx_pasien` (`id_pasien`),
  ADD KEY `idx_tanggal` (`tanggal_janji`),
  ADD KEY `idx_janji_temu_tanggal_waktu` (`tanggal_janji`,`waktu_janji`),
  ADD KEY `idx_janji_temu_status` (`status`);

--
-- Indeks untuk tabel `pasien`
--
ALTER TABLE `pasien`
  ADD PRIMARY KEY (`id_pasien`);

--
-- Indeks untuk tabel `rekam_medis`
--
ALTER TABLE `rekam_medis`
  ADD PRIMARY KEY (`id_rekam`),
  ADD KEY `idx_pasien_tanggal` (`id_pasien`,`tanggal`),
  ADD KEY `idx_rekam_medis_tanggal` (`tanggal`);

--
-- Indeks untuk tabel `statistik_kunjungan`
--
ALTER TABLE `statistik_kunjungan`
  ADD PRIMARY KEY (`id_kunjungan`),
  ADD KEY `idx_tanggal` (`tanggal_kunjungan`),
  ADD KEY `idx_pasien` (`id_pasien`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `antrian`
--
ALTER TABLE `antrian`
  MODIFY `id_antrian` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT untuk tabel `antrian_selesai`
--
ALTER TABLE `antrian_selesai`
  MODIFY `id_selesai` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT untuk tabel `janji`
--
ALTER TABLE `janji`
  MODIFY `id_janji` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT untuk tabel `janji_temu`
--
ALTER TABLE `janji_temu`
  MODIFY `id_janji_temu` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `statistik_kunjungan`
--
ALTER TABLE `statistik_kunjungan`
  MODIFY `id_kunjungan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
