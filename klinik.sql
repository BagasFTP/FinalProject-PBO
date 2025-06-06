-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 06 Jun 2025 pada 18.32
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

    -- Insert ke tabel janji_temu
    INSERT INTO janji_temu (id_pasien, tanggal_janji, waktu_janji, status)
    VALUES (p_id_pasien, p_tanggal_janji, p_waktu_janji, 'Menunggu');

    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetNamaPasien` (IN `p_id_pasien` VARCHAR(20))   BEGIN
    SELECT nama_pasien FROM pasien WHERE id_pasien = p_id_pasien;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SelesaikanAntrian` (IN `p_id_antrian` INT, IN `p_id_pasien` VARCHAR(20), IN `p_tanggal_antrian` DATE)   BEGIN
    -- Masukkan ke tabel antrian_selesai
    INSERT INTO antrian_selesai (id_pasien, tanggal_selesai, jenis_kunjungan)
    VALUES (p_id_pasien, p_tanggal_antrian, 'konsultasi');

    -- Hapus dari tabel antrian
    DELETE FROM antrian WHERE id_antrian = p_id_antrian;

    -- Tambahkan ke statistik_kunjungan
    INSERT INTO statistik_kunjungan (id_pasien, tanggal_kunjungan, jenis_kunjungan)
    VALUES (p_id_pasien, p_tanggal_antrian, 'konsultasi')
    ON DUPLICATE KEY UPDATE tanggal_kunjungan = tanggal_kunjungan; -- Ini asumsikan ada UNIQUE KEY pada (id_pasien, tanggal_kunjungan)
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SelesaikanJanjiTemu` (IN `p_id_janji_temu` INT, IN `p_diagnosis` TEXT, IN `p_tindakan` TEXT, IN `p_obat` TEXT)   BEGIN
    DECLARE v_id_pasien VARCHAR(20);
    DECLARE v_tanggal_janji DATE;
    DECLARE v_waktu_janji TIME;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Ambil detail janji temu
    SELECT id_pasien, tanggal_janji, waktu_janji
    INTO v_id_pasien, v_tanggal_janji, v_waktu_janji
    FROM janji_temu
    WHERE id_janji_temu = p_id_janji_temu;

    -- Jika janji temu ditemukan, masukkan ke rekam medis
    IF v_id_pasien IS NOT NULL THEN
        INSERT INTO rekam_medis (id_pasien, id_janji_temu, tanggal, diagnosis, tindakan, obat)
        VALUES (v_id_pasien, p_id_janji_temu, CURDATE(), p_diagnosis, p_tindakan, p_obat);

        -- Update status janji temu menjadi 'Dilayani'
        UPDATE janji_temu
        SET status = 'Dilayani'
        WHERE id_janji_temu = p_id_janji_temu;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Janji Temu tidak ditemukan.';
    END IF;

    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateStatusJanjiTemu` (IN `p_id_janji_temu` INT, IN `p_status_baru` VARCHAR(50))   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    UPDATE janji_temu
    SET status = p_status_baru
    WHERE id_janji_temu = p_id_janji_temu;

    COMMIT;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `antrian`
--

CREATE TABLE `antrian` (
  `id_antrian` int(11) NOT NULL,
  `id_pasien` varchar(20) DEFAULT NULL,
  `tanggal_antrian` date DEFAULT NULL,
  `nomor_antrian` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'waiting',
  `id_janji` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `antrian_selesai`
--

CREATE TABLE `antrian_selesai` (
  `id_selesai` int(11) NOT NULL,
  `id_pasien` varchar(20) DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jenis_kunjungan` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `janji_temu`
--

CREATE TABLE `janji_temu` (
  `id_janji_temu` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `tanggal_janji` date NOT NULL,
  `waktu_janji` time NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'Menunggu'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pasien`
--

CREATE TABLE `pasien` (
  `id_pasien` varchar(20) NOT NULL,
  `nama_pasien` varchar(100) NOT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `jenis_kelamin` enum('Laki-laki','Perempuan') DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pasien`
--

INSERT INTO `pasien` (`id_pasien`, `nama_pasien`, `tanggal_lahir`, `jenis_kelamin`, `alamat`, `telepon`) VALUES
('P001', 'Devinka Marta Legawa', '2024-03-04', 'Laki-laki', 'Jl. Gajahmada, Sidoarjo', '0089523483603');

-- --------------------------------------------------------

--
-- Struktur dari tabel `rekam_medis`
--

CREATE TABLE `rekam_medis` (
  `id_rekam` int(11) NOT NULL,
  `id_pasien` varchar(20) NOT NULL,
  `id_janji_temu` int(11) DEFAULT NULL,
  `tanggal` date NOT NULL,
  `diagnosis` text DEFAULT NULL,
  `tindakan` text DEFAULT NULL,
  `obat` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `statistik_kunjungan`
--

CREATE TABLE `statistik_kunjungan` (
  `id_kunjungan` int(11) NOT NULL,
  `id_pasien` varchar(20) DEFAULT NULL,
  `tanggal_kunjungan` date DEFAULT NULL,
  `jenis_kunjungan` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `antrian`
--
ALTER TABLE `antrian`
  ADD PRIMARY KEY (`id_antrian`),
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `fk_antrian_janji` (`id_janji`);

--
-- Indeks untuk tabel `antrian_selesai`
--
ALTER TABLE `antrian_selesai`
  ADD PRIMARY KEY (`id_selesai`),
  ADD KEY `id_pasien` (`id_pasien`);

--
-- Indeks untuk tabel `janji_temu`
--
ALTER TABLE `janji_temu`
  ADD PRIMARY KEY (`id_janji_temu`),
  ADD KEY `id_pasien` (`id_pasien`);

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
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `id_janji_temu` (`id_janji_temu`);

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
  MODIFY `id_antrian` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `antrian_selesai`
--
ALTER TABLE `antrian_selesai`
  MODIFY `id_selesai` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `janji_temu`
--
ALTER TABLE `janji_temu`
  MODIFY `id_janji_temu` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `rekam_medis`
--
ALTER TABLE `rekam_medis`
  MODIFY `id_rekam` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `statistik_kunjungan`
--
ALTER TABLE `statistik_kunjungan`
  MODIFY `id_kunjungan` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `antrian`
--
ALTER TABLE `antrian`
  ADD CONSTRAINT `antrian_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`),
  ADD CONSTRAINT `fk_antrian_janji` FOREIGN KEY (`id_janji`) REFERENCES `janji_temu` (`id_janji_temu`);

--
-- Ketidakleluasaan untuk tabel `antrian_selesai`
--
ALTER TABLE `antrian_selesai`
  ADD CONSTRAINT `antrian_selesai_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`);

--
-- Ketidakleluasaan untuk tabel `janji_temu`
--
ALTER TABLE `janji_temu`
  ADD CONSTRAINT `janji_temu_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`);

--
-- Ketidakleluasaan untuk tabel `rekam_medis`
--
ALTER TABLE `rekam_medis`
  ADD CONSTRAINT `rekam_medis_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`),
  ADD CONSTRAINT `rekam_medis_ibfk_2` FOREIGN KEY (`id_janji_temu`) REFERENCES `janji_temu` (`id_janji_temu`);

--
-- Ketidakleluasaan untuk tabel `statistik_kunjungan`
--
ALTER TABLE `statistik_kunjungan`
  ADD CONSTRAINT `statistik_kunjungan_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
