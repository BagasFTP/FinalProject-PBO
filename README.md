# 🦷 Aplikasi Klinik Dokter Gigi – Desktop App

Aplikasi desktop untuk **manajemen klinik dokter gigi**, dikembangkan menggunakan **Java Swing** dan **MySQL**. Aplikasi ini mempermudah kegiatan operasional di klinik seperti registrasi pasien, pengelolaan antrian, janji temu, hingga pencatatan rekam medis dan pembuatan laporan.

---

## 🚀 Fitur Utama

### 📋 Registrasi Pasien
Mendaftarkan pasien baru dengan data lengkap seperti nama, tanggal lahir, alamat, dan nomor telepon. Data pasien tersimpan ke database untuk keperluan pelayanan dan pencatatan medis.

### ⏳ Manajemen Antrian Pasien
Petugas dapat menambahkan pasien ke dalam daftar antrian dan memanggil pasien berdasarkan urutan. Sistem ini membantu menjaga alur layanan lebih tertib.

### 📅 Manajemen Janji Pasien
Fitur untuk membuat dan mengatur janji temu antara pasien dan dokter. Janji dapat disesuaikan berdasarkan tanggal dan waktu yang tersedia.

### 🔍 Cek Tanggal Janji
Menampilkan seluruh janji temu berdasarkan tanggal tertentu agar petugas dapat melihat siapa saja yang akan datang di hari tersebut.

### ⏰ Reminder Janji Temu
Menyediakan pengingat janji temu secara visual melalui tampilan jadwal yang dapat diakses kapan saja.

### 📈 Statistik Kunjungan
Menampilkan jumlah kunjungan pasien harian, mingguan, atau bulanan dalam bentuk statistik sederhana.

### 📝 Rekam Medis Pasien
Dokter dapat mencatat hasil pemeriksaan dan tindakan medis yang telah dilakukan kepada pasien. Data ini tersimpan dan dapat dilihat kembali.

### 📄 Export Laporan ke PDF
Fitur untuk mengekspor data penting seperti kunjungan, rekam medis, atau daftar janji temu ke dalam format PDF yang rapi dan siap cetak.

---

## 🛠 Teknologi yang Digunakan

| Komponen           | Teknologi                   |
|--------------------|-----------------------------|
| Bahasa Pemrograman | Java                        |
| GUI Framework      | Java Swing                  |
| Database           | MySQL                       |
| PDF Export         | iText / OpenPDF             |

---

## 🧑‍💻 Cara Penggunaan Aplikasi

### 1. Instalasi

1. Pastikan Java JDK dan MySQL sudah terinstal di komputer Anda.
2. Clone atau download project ini ke komputer Anda.
3. Import ke IDE seperti IntelliJ IDEA, VS Code atau NetBeans (Disarankan Menggunakan Intellij IDEA).
4. Pastikan file koneksi database (`config.koneksi`) mengarah ke database Anda.
5. Jalankan file `MainApp.java` sebagai entry point aplikasi.

### 2. Setup Database

1. Buat database `klinik` di MySQL.
2. Import file SQL yang tersedia untuk membuat tabel-tabel penting seperti `pasien`, `rekam_medis`, `antrian`, dll.
3. Sesuaikan konfigurasi koneksi database di file `koneksi.java`.

### 3. Navigasi Aplikasi

- **Login**: Masuk dengan akun petugas atau admin.
- **Registrasi Pasien**: Masuk ke menu *Registrasi* untuk menambah data pasien.
- **Antrian**: Tambah pasien ke daftar antrian dan kelola pemanggilan.
- **Janji Temu**: Jadwalkan janji antara pasien dan dokter.
- **Rekam Medis**: Masuk ke menu *Rekam Medis* untuk mengisi catatan pemeriksaan pasien.
- **Laporan PDF**: Gunakan fitur export untuk menyimpan laporan sebagai file PDF.

---

## 📌 Catatan

- Pastikan koneksi database aktif sebelum menjalankan aplikasi (Gunakan mysql-connector).
- Pastikan sudah melakukan konfigurasi iText pdf
- Data PDF tersimpan di lokasi default atau sesuai dengan path yang ditentukan di dalam program.
- Anda dapat menyesuaikan tampilan dan fitur dengan memodifikasi file di folder `view`, `controller`, dan `model`.

---
