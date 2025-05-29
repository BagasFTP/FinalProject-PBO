package view;

import controller.AntrianController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormAntrian extends JFrame {
    private JTable tableAntrian;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnRefresh, btnSelesai;

    public FormAntrian() {
        setTitle("Antrian Pasien");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buatHeader(), BorderLayout.NORTH);
        add(buatKonten(), BorderLayout.CENTER);
        add(buatPanelTombol(), BorderLayout.SOUTH);

        refreshAntrian();
        setVisible(true);
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

    private JScrollPane buatKonten() {
        String[] kolom = { "No", "ID Janji", "ID Pasien", "Tanggal Antrian" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // semua sel tidak bisa diedit
            }
        };

        tableAntrian = new JTable(tableModel);
        tableAntrian.setRowHeight(24);
        tableAntrian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableAntrian.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tableAntrian);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Data Antrian"));
        return scrollPane;
    }

    private JPanel buatPanelTombol() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnTambah = new JButton("Tambah Antrian");
        btnRefresh = new JButton("Refresh");
        btnSelesai = new JButton("Selesaikan Antrian");

        btnTambah.setBackground(new Color(45, 85, 125));
        btnTambah.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(180, 180, 180));
        btnSelesai.setBackground(new Color(200, 70, 70));
        btnSelesai.setForeground(Color.WHITE);

        btnTambah.setFocusPainted(false);
        btnRefresh.setFocusPainted(false);
        btnSelesai.setFocusPainted(false);

        panel.add(btnTambah);
        panel.add(btnRefresh);
        panel.add(btnSelesai);

        // Action
        btnTambah.addActionListener(e -> tambahAntrian());
        btnRefresh.addActionListener(e -> refreshAntrian());
        btnSelesai.addActionListener(e -> selesaikanAntrian());

        return panel;
    }

    private void tambahAntrian() {
        String idPasien = JOptionPane.showInputDialog(this, "Masukkan ID Pasien:");
        if (idPasien != null && !idPasien.trim().isEmpty()) {
            try {
                AntrianController.tambahAntrian(idPasien.trim());
                JOptionPane.showMessageDialog(this, "Antrian ditambahkan.");
                refreshAntrian();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan antrian: " + ex.getMessage());
            }
        }
    }

    private void refreshAntrian() {
        tableModel.setRowCount(0);
        try {
            List<String[]> daftar = AntrianController.getDaftarAntrian();
            for (int i = 0; i < daftar.size(); i++) {
                String[] row = daftar.get(i); // [id_janji, id_pasien, tanggal_janji]
                tableModel.addRow(new Object[] {
                        i + 1, row[0], row[1], row[2]
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data antrian.");
        }
    }

    private void selesaikanAntrian() {
        String idJanji = JOptionPane.showInputDialog(this, "Masukkan ID JANJI yang akan diselesaikan:");
        if (idJanji != null && !idJanji.trim().isEmpty()) {
            try {
                List<String[]> daftar = AntrianController.getDaftarAntrian();
                String idPasien = null;
                for (String[] row : daftar) {
                    if (row[0].equals(idJanji.trim())) {
                        idPasien = row[1];
                        break;
                    }
                }

                if (idPasien != null) {
                    AntrianController.hapusAntrianDanCatat(idJanji.trim(), idPasien);
                    JOptionPane.showMessageDialog(this, "Antrian telah diselesaikan dan dicatat.");
                    refreshAntrian();
                } else {
                    JOptionPane.showMessageDialog(this, "ID janji tidak ditemukan!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menyelesaikan antrian: " + ex.getMessage());
            }
        }
    }
}
