package view;

import config.koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormCekTanggal extends JPanel {
    private final JSpinner spinnerTanggal;
    private final JTable tableJanjiTemu;
    private final JTable tableKunjungan;
    private final DefaultTableModel modelJanjiTemu;
    private final DefaultTableModel modelKunjungan;

    public FormCekTanggal() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Konten Tengah
        JPanel isiPanel = new JPanel();
        isiPanel.setLayout(new BoxLayout(isiPanel, BoxLayout.Y_AXIS));
        isiPanel.setBackground(Color.WHITE);

        JPanel inputPanel = createInputPanel();
        isiPanel.add(inputPanel);

        JScrollPane scrollJanji = createTableScrollPane(modelJanjiTemu = new DefaultTableModel(
                new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Waktu", "Status"}, 0),
                tableJanjiTemu = new JTable(modelJanjiTemu), "Janji Temu Hari Ini");
        isiPanel.add(scrollJanji);

        JScrollPane scrollKunjungan = createTableScrollPane(modelKunjungan = new DefaultTableModel(
                new String[]{"ID Pasien", "Nama Pasien", "Jenis Kunjungan"}, 0),
                tableKunjungan = new JTable(modelKunjungan), "Kunjungan Pasien Hari Ini");
        isiPanel.add(scrollKunjungan);

        JScrollPane scrollIsi = new JScrollPane(isiPanel);
        scrollIsi.setBorder(null);
        add(scrollIsi, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Cek Pasien & Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createInputPanel() {
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
        btnCari.addActionListener(e -> cariDataTanggal());

        inputPanel.add(lTgl);
        inputPanel.add(spinnerTanggal);
        inputPanel.add(btnCari);

        return inputPanel;
    }

    private JScrollPane createTableScrollPane(DefaultTableModel model, JTable table, String title) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        scroll.setPreferredSize(new Dimension(800, 200));
        return scroll;
    }

    private void cariDataTanggal() {
        Date selectedDate = (Date) spinnerTanggal.getValue();
        String tanggalCari = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        modelJanjiTemu.setRowCount(0);
        modelKunjungan.setRowCount(0);
        boolean adaData = false;

        adaData |= loadJanjiTemu(tanggalCari);
        adaData |= loadKunjungan(tanggalCari);

        if (!adaData) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data (janji temu atau kunjungan) pada tanggal tersebut.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean loadJanjiTemu(String tanggalCari) {
        String query = "SELECT jt.id_janji_temu, p.id_pasien, p.nama_pasien, jt.waktu_janji, jt.status " +
                "FROM janji_temu jt " +
                "JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                "WHERE jt.tanggal_janji = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, tanggalCari);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                modelJanjiTemu.addRow(new Object[]{
                        rs.getInt("id_janji_temu"),
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getTime("waktu_janji"),
                        rs.getString("status")
                });
                found = true;
            }
            return found;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching janji temu: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean loadKunjungan(String tanggalCari) {
        String query = "SELECT sk.id_pasien, p.nama_pasien, sk.jenis_kunjungan " +
                "FROM statistik_kunjungan sk " +
                "JOIN pasien p ON sk.id_pasien = p.id_pasien " +
                "WHERE sk.tanggal_kunjungan = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, tanggalCari);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                modelKunjungan.addRow(new Object[]{
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getString("jenis_kunjungan")
                });
                found = true;
            }
            return found;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching kunjungan pasien: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
