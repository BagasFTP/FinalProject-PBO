package view;

import config.koneksi; // Assuming you have a config.koneksi class for database connection

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class FormCekTanggal extends JFrame {
    private JSpinner spinnerTanggal;
    private JTextArea resultArea; // Added JTextArea to display results

    public FormCekTanggal() {
        setTitle("Cek Pasien & Janji Berdasarkan Tanggal");
        setSize(600, 400); // Increased size for better display of results
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better layout management
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Cek Pasien & Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        inputPanel.setBackground(Color.WHITE);
        
        JLabel lTgl = new JLabel("Pilih Tanggal:");
        lTgl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd"));
        spinnerTanggal.setPreferredSize(new Dimension(150, 30));

        JButton btnCari = new JButton("Cari Data");
        btnCari.setBackground(new Color(46, 204, 113));
        btnCari.setForeground(Color.WHITE);
        btnCari.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCari.setFocusPainted(false);

        inputPanel.add(lTgl);
        inputPanel.add(spinnerTanggal);
        inputPanel.add(btnCari);
        add(inputPanel, BorderLayout.CENTER);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Hasil Pencarian",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.DARK_GRAY));
        add(scrollPane, BorderLayout.SOUTH);

        scrollPane.setPreferredSize(new Dimension(getWidth(), 250));

        btnCari.addActionListener(e -> cariDataTanggal());

        setVisible(true);
    }

    private void cariDataTanggal() {
        Date selectedDate = (Date) spinnerTanggal.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalCari = sdf.format(selectedDate);
        resultArea.setText(""); // Clear previous results

        StringBuilder hasil = new StringBuilder();

        // 1. Check for patients with appointments (janji_temu)
        hasil.append("--- Janji Temu Hari Ini ---\n");
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement psJanji = conn.prepareStatement(
                "SELECT jt.id_janji_temu, p.id_pasien, p.nama_pasien, jt.waktu_janji, jt.status " +
                "FROM janji_temu jt " +
                "JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                "WHERE jt.tanggal_janji = ?"
             )) {
            psJanji.setString(1, tanggalCari);
            ResultSet rsJanji = psJanji.executeQuery();

            if (!rsJanji.isBeforeFirst()) { // Check if ResultSet is empty
                hasil.append("Tidak ada janji temu pada tanggal tersebut.\n");
            } else {
                while (rsJanji.next()) {
                    int idJanji = rsJanji.getInt("id_janji_temu");
                    String idPasien = rsJanji.getString("id_pasien");
                    String namaPasien = rsJanji.getString("nama_pasien");
                    Time waktuJanji = rsJanji.getTime("waktu_janji");
                    String statusJanji = rsJanji.getString("status");
                    hasil.append(String.format("Janji ID: %d, Pasien ID: %s, Nama: %s, Waktu: %s, Status: %s\n",
                            idJanji, idPasien, namaPasien, waktuJanji, statusJanji));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching appointment data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        hasil.append("\n--- Kunjungan Pasien Hari Ini ---\n");
        // 2. Check for patient visits (statistik_kunjungan)
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement psKunjungan = conn.prepareStatement(
                "SELECT sk.id_pasien, p.nama_pasien, sk.jenis_kunjungan " + // Tambahkan spasi di akhir dan gunakan alias 'p'
                "FROM statistik_kunjungan sk " +
                "JOIN pasien p ON sk.id_pasien = p.id_pasien " + // Gunakan alias 'p' di sini
                "WHERE sk.tanggal_kunjungan = ?"
             )) {
            psKunjungan.setString(1, tanggalCari);
            ResultSet rsKunjungan = psKunjungan.executeQuery();

            if (!rsKunjungan.isBeforeFirst()) { // Check if ResultSet is empty
                hasil.append("Tidak ada riwayat kunjungan pada tanggal tersebut.\n");
            } else {
                while (rsKunjungan.next()) {
                    String id = rsKunjungan.getString("id_pasien");
                    String nama = rsKunjungan.getString("nama_pasien");
                    String jenisKunjungan = rsKunjungan.getString("jenis_kunjungan");
                    hasil.append(String.format("Pasien ID: %s, Nama: %s, Jenis Kunjungan: %s\n",
                            id, nama, jenisKunjungan));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching visit data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        resultArea.setText(hasil.toString());
        if (hasil.toString().contains("Tidak ada") && !hasil.toString().contains("Janji ID")) {
            JOptionPane.showMessageDialog(this, "Tidak ada data (janji temu atau kunjungan) pada tanggal tersebut.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Connector 8.x
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan. Pastikan Anda telah menambahkan library JDBC MySQL.", "Error Driver", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(FormCekTanggal::new);
    }
}