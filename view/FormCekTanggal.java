package view;

import config.koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormCekTanggal extends JPanel { // Change from JFrame to JPanel
    private JSpinner spinnerTanggal;
    private JTable tableJanjiTemu;
    private JTable tableKunjungan;
    private DefaultTableModel modelJanjiTemu;
    private DefaultTableModel modelKunjungan;

    public FormCekTanggal() {
        // Remove JFrame specific settings
        // setTitle("Cek Pasien & Janji Berdasarkan Tanggal");
        // setSize(900, 700);
        // setLocationRelativeTo(null);
        // setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel utama (this JPanel itself)
        setLayout(new BorderLayout(10, 10)); // Apply layout to this JPanel
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        // setContentPane(panelUtama); // No longer needed

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Cek Pasien & Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH); // Add to this JPanel

        // Panel isi (tengah)
        JPanel isiPanel = new JPanel();
        isiPanel.setLayout(new BoxLayout(isiPanel, BoxLayout.Y_AXIS));
        isiPanel.setBackground(Color.WHITE);

        // Input panel
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

        isiPanel.add(inputPanel);

        // Tabel Janji Temu
        modelJanjiTemu = new DefaultTableModel(
                new String[] { "ID Janji", "ID Pasien", "Nama Pasien", "Waktu", "Status" }, 0);
        tableJanjiTemu = new JTable(modelJanjiTemu);
        JScrollPane scrollJanji = new JScrollPane(tableJanjiTemu);
        scrollJanji.setBorder(BorderFactory.createTitledBorder("Janji Temu Hari Ini"));
        scrollJanji.setPreferredSize(new Dimension(800, 200));
        isiPanel.add(scrollJanji);

        // Tabel Kunjungan
        modelKunjungan = new DefaultTableModel(new String[] { "ID Pasien", "Nama Pasien", "Jenis Kunjungan" }, 0);
        tableKunjungan = new JTable(modelKunjungan);
        JScrollPane scrollKunjungan = new JScrollPane(tableKunjungan);
        scrollKunjungan.setBorder(BorderFactory.createTitledBorder("Kunjungan Pasien Hari Ini"));
        scrollKunjungan.setPreferredSize(new Dimension(800, 200));
        isiPanel.add(scrollKunjungan);

        // Scroll untuk isiPanel
        JScrollPane scrollIsi = new JScrollPane(isiPanel);
        scrollIsi.setBorder(null); // opsional
        add(scrollIsi, BorderLayout.CENTER); // Add to this JPanel

        // Event
        btnCari.addActionListener(e -> cariDataTanggal());

        // setVisible(true); // No longer needed for a JPanel
    }

    private void cariDataTanggal() {
        Date selectedDate = (Date) spinnerTanggal.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalCari = sdf.format(selectedDate);

        // Kosongkan tabel sebelum menambahkan data baru
        modelJanjiTemu.setRowCount(0);
        modelKunjungan.setRowCount(0);

        boolean adaData = false;

        // Query Janji Temu
        try (Connection conn = koneksi.getKoneksi();
                PreparedStatement psJanji = conn.prepareStatement(
                        "SELECT jt.id_janji_temu, p.id_pasien, p.nama_pasien, jt.waktu_janji, jt.status " +
                                "FROM janji_temu jt " +
                                "JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                                "WHERE jt.tanggal_janji = ?")) {

            psJanji.setString(1, tanggalCari);
            ResultSet rsJanji = psJanji.executeQuery();

            while (rsJanji.next()) {
                adaData = true;
                modelJanjiTemu.addRow(new Object[] {
                        rsJanji.getInt("id_janji_temu"),
                        rsJanji.getString("id_pasien"),
                        rsJanji.getString("nama_pasien"),
                        rsJanji.getTime("waktu_janji").toString(),
                        rsJanji.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching janji temu: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Query Kunjungan Pasien
        try (Connection conn = koneksi.getKoneksi();
                PreparedStatement psKunjungan = conn.prepareStatement(
                        "SELECT sk.id_pasien, p.nama_pasien, sk.jenis_kunjungan " +
                                "FROM statistik_kunjungan sk " +
                                "JOIN pasien p ON sk.id_pasien = p.id_pasien " +
                                "WHERE sk.tanggal_kunjungan = ?")) {

            psKunjungan.setString(1, tanggalCari);
            ResultSet rsKunjungan = psKunjungan.executeQuery();

            while (rsKunjungan.next()) {
                adaData = true;
                modelKunjungan.addRow(new Object[] {
                        rsKunjungan.getString("id_pasien"),
                        rsKunjungan.getString("nama_pasien"),
                        rsKunjungan.getString("jenis_kunjungan")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching kunjungan pasien: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!adaData) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data (janji temu atau kunjungan) pada tanggal tersebut.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // The main method is no longer necessary as this will be a panel
    // and will be instantiated by MainApp.
    // public static void main(String[] args) {
    //     try {
    //         Class.forName("com.mysql.cj.jdbc.Driver");
    //     } catch (ClassNotFoundException e) {
    //         JOptionPane.showMessageDialog(null,
    //                 "Driver MySQL tidak ditemukan. Pastikan Anda telah menambahkan library JDBC MySQL.",
    //                 "Error Driver", JOptionPane.ERROR_MESSAGE);
    //         return;
    //     }
    //
    //     SwingUtilities.invokeLater(FormCekTanggal::new);
    // }
}