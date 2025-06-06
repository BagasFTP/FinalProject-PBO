package view;

import controller.AntrianController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.koneksi;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;

public class FormAntrian extends JPanel {
    private JTable tableAntrian;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnRefresh, btnSelesai, btnPanggil;
    private JTextField tfIdPasienAntrian;

    public FormAntrian() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buatHeader(), BorderLayout.NORTH);
        add(buatKonten(), BorderLayout.CENTER);
        add(buatPanelTombol(), BorderLayout.SOUTH);

        refreshAntrian();
    }

    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 85, 125));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblJudul = new JLabel("DAFTAR ANTRIAN PASIEN");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblJudul.setForeground(Color.WHITE);

        header.add(lblJudul, BorderLayout.WEST);
        return header;
    }

    private JPanel buatKonten() {
        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelKonten.setBackground(Color.WHITE);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tambah Antrian Baru"));
        JLabel lblIdPasien = new JLabel("ID Pasien:");
        tfIdPasienAntrian = new JTextField(15);
        btnTambah = new JButton("Tambah Antrian");
        btnTambah.setBackground(new Color(52, 152, 219));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahAntrianBaru());

        inputPanel.add(lblIdPasien);
        inputPanel.add(tfIdPasienAntrian);
        inputPanel.add(btnTambah);
        panelKonten.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Antrian", "ID Pasien", "Nama Pasien", "Tanggal Antrian", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAntrian = new JTable(tableModel);
        tableAntrian.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAntrian.getTableHeader().setReorderingAllowed(false);
        tableAntrian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableAntrian.setRowHeight(25);
        tableAntrian.getTableHeader().setBackground(new Color(52, 152, 219));
        tableAntrian.getTableHeader().setForeground(Color.WHITE);
        tableAntrian.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tableAntrian);
        panelKonten.add(scrollPane, BorderLayout.CENTER);

        return panelKonten;
    }

    private JPanel buatPanelTombol() {
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelTombol.setBackground(Color.WHITE);

        btnPanggil = new JButton("Panggil Antrian");
        btnPanggil.setBackground(new Color(241, 196, 15));
        btnPanggil.setForeground(Color.BLACK);
        btnPanggil.setFocusPainted(false);
        btnPanggil.addActionListener(e -> panggilAntrian());

        btnSelesai = new JButton("Selesaikan Antrian");
        btnSelesai.setBackground(new Color(46, 204, 113));
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.addActionListener(e -> selesaikanAntrian());

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshAntrian());

        panelTombol.add(btnPanggil);
        panelTombol.add(btnSelesai);
        panelTombol.add(btnRefresh);

        return panelTombol;
    }

    private void refreshAntrian() {
        tableModel.setRowCount(0);
        try {
            List<String[]> daftar = AntrianController.getDaftarAntrian();
            for (String[] antrian : daftar) {
                tableModel.addRow(antrian);
            }
            tfIdPasienAntrian.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error refreshing antrian: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void tambahAntrianBaru() {
        String idPasien = tfIdPasienAntrian.getText().trim();
        if (idPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Pasien tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!isPasienIdExists(idPasien)) {
                JOptionPane.showMessageDialog(this, "ID Pasien tidak ditemukan.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int nextNomorAntrian = AntrianController.getNextNomorAntrian(new Date());

            AntrianController.tambahAntrian(idPasien, new Date(), nextNomorAntrian);
            JOptionPane.showMessageDialog(this, "Antrian untuk pasien " + idPasien + " berhasil ditambahkan dengan nomor " + nextNomorAntrian + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshAntrian();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan antrian: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void panggilAntrian() {
        int selectedRow = tableAntrian.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih antrian yang ingin dipanggil dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAntrian = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String idPasien = tableModel.getValueAt(selectedRow, 1).toString();
        String namaPasien = tableModel.getValueAt(selectedRow, 2).toString();

        try {
            AntrianController.panggilAntrian(idAntrian);
            JOptionPane.showMessageDialog(this, "Antrian untuk pasien " + namaPasien + " (ID: " + idPasien + ") dipanggil.", "Antrian Dipanggil", JOptionPane.INFORMATION_MESSAGE);
            refreshAntrian();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memanggil antrian: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void selesaikanAntrian() {
        int selectedRow = tableAntrian.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih antrian yang ingin diselesaikan dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAntrian = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String idPasienFromTable = tableModel.getValueAt(selectedRow, 1).toString();
        String namaPasienFromTable = tableModel.getValueAt(selectedRow, 2).toString();

        try {
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Selesaikan antrian untuk:\n" +
                    "ID Antrian: " + idAntrian + "\n" +
                    "ID Pasien: " + idPasienFromTable + "\n" +
                    "Nama: " + namaPasienFromTable + "\n\n" +
                    "Apakah Anda yakin?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                AntrianController.selesaikanAntrian(idAntrian);
                JOptionPane.showMessageDialog(this,
                    "Antrian telah diselesaikan dan dicatat.",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAntrian();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyelesaikan antrian: " + ex.getMessage(),
                "Error Database",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan tidak terduga: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean isPasienIdExists(String idPasien) {
        String query = "SELECT COUNT(*) FROM pasien WHERE id_pasien = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}