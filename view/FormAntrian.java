package view;

import controller.AntrianController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormAntrian extends JPanel {
    private JTable tableAntrian;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnRefresh, btnSelesai;

    public FormAntrian() {
        setLayout(new BorderLayout());

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

    private JScrollPane buatKonten() {
        String[] kolom = { "No", "ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Antrian", "Status" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableAntrian = new JTable(tableModel);
        tableAntrian.setRowHeight(25);
        tableAntrian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableAntrian.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        tableAntrian.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableAntrian.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableAntrian.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableAntrian.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableAntrian.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableAntrian.getColumnModel().getColumn(5).setPreferredWidth(80);

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

        btnTambah.addActionListener(e -> tambahAntrian());
        btnRefresh.addActionListener(e -> refreshAntrian());
        btnSelesai.addActionListener(e -> selesaikanAntrian());

        return panel;
    }

    private void tambahAntrian() {
        String idPasien = JOptionPane.showInputDialog(this, 
            "Masukkan ID Pasien:", 
            "Tambah Antrian", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (idPasien != null && !idPasien.trim().isEmpty()) {
            try {
                if (!AntrianController.cekPasienAda(idPasien.trim())) {
                    JOptionPane.showMessageDialog(this, 
                        "ID Pasien tidak ditemukan di database!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                AntrianController.tambahAntrian(idPasien.trim());
                JOptionPane.showMessageDialog(this, 
                    "Antrian berhasil ditambahkan.", 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAntrian();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Gagal menambahkan antrian: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAntrian() {
        tableModel.setRowCount(0);
        try {
            List<String[]> daftar = AntrianController.getDaftarAntrian();
            
            for (int i = 0; i < daftar.size(); i++) {
                String[] row = daftar.get(i); 
                tableModel.addRow(new Object[] {
                    i + 1, row[0], row[1], row[2], row[3], row[4]
                });
            }
            // <- Perubahan 2: Baris setTitle(...) dihapus dari sini.
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat data antrian: " + e.getMessage(), 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error saat refresh antrian: " + e.getMessage());
        }
    }

    private void selesaikanAntrian() {
        int selectedRow = tableAntrian.getSelectedRow();
        if (selectedRow == -1) {
            String idPasien = JOptionPane.showInputDialog(this, 
                "Masukkan ID Pasien yang akan diselesaikan:", 
                "Selesaikan Antrian", 
                JOptionPane.QUESTION_MESSAGE);
                
            if (idPasien == null || idPasien.trim().isEmpty()) {
                return;
            }
            prosesSelesaikanAntrian(idPasien.trim());
        } else {
            String idJanji = tableModel.getValueAt(selectedRow, 1).toString();
            String idPasien = tableModel.getValueAt(selectedRow, 2).toString();
            String namaPasien = tableModel.getValueAt(selectedRow, 3).toString();
            
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Selesaikan antrian untuk:\n" +
                "ID Pasien: " + idPasien + "\n" +
                "Nama: " + namaPasien + "\n\n" +
                "Apakah Anda yakin?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    AntrianController.hapusAntrianDanCatat(idPasien, idJanji);
                    JOptionPane.showMessageDialog(this, 
                        "Antrian telah diselesaikan dan dicatat.", 
                        "Sukses", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshAntrian();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Gagal menyelesaikan antrian: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void prosesSelesaikanAntrian(String idPasien) {
        try {
            List<String[]> daftar = AntrianController.getDaftarAntrian();
            String idJanji = null;
            String namaPasien = null;
            
            for (String[] row : daftar) {
                if (row[1].equals(idPasien)) {
                    idJanji = row[0];
                    namaPasien = row[2];
                    break;
                }
            }

            if (idJanji != null) {
                int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Selesaikan antrian untuk:\n" +
                    "ID Pasien: " + idPasien + "\n" +
                    "Nama: " + namaPasien + "\n\n" +
                    "Apakah Anda yakin?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
                    
                if (konfirmasi == JOptionPane.YES_OPTION) {
                    AntrianController.hapusAntrianDanCatat(idPasien, idJanji);
                    JOptionPane.showMessageDialog(this, 
                        "Antrian telah diselesaikan dan dicatat.", 
                        "Sukses", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshAntrian();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "ID Pasien tidak ditemukan dalam antrian!", 
                    "Data Tidak Ditemukan", 
                    JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Gagal menyelesaikan antrian: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}