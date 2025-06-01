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
        setSize(700, 500);
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
        String[] kolom = { "No", "ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Antrian", "Status" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // semua sel tidak bisa diedit
            }
        };

        tableAntrian = new JTable(tableModel);
        tableAntrian.setRowHeight(25);
        tableAntrian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableAntrian.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths
        tableAntrian.getColumnModel().getColumn(0).setPreferredWidth(50);  // No
        tableAntrian.getColumnModel().getColumn(1).setPreferredWidth(80);  // ID Janji
        tableAntrian.getColumnModel().getColumn(2).setPreferredWidth(80);  // ID Pasien
        tableAntrian.getColumnModel().getColumn(3).setPreferredWidth(150); // Nama Pasien
        tableAntrian.getColumnModel().getColumn(4).setPreferredWidth(120); // Tanggal
        tableAntrian.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status

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

        // Action listeners
        btnTambah.addActionListener(e -> tambahAntrian());
        btnRefresh.addActionListener(e -> refreshAntrian());
        btnSelesai.addActionListener(e -> selesaikanAntrian());

        return panel;
    }

    private void tambahAntrian() {
        // Dialog untuk input ID Pasien
        String idPasien = JOptionPane.showInputDialog(this, 
            "Masukkan ID Pasien:", 
            "Tambah Antrian", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (idPasien != null && !idPasien.trim().isEmpty()) {
            try {
                // Validasi apakah pasien ada
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
            
            if (daftar.isEmpty()) {
                // Tampilkan pesan jika tidak ada data
                JLabel lblKosong = new JLabel("Tidak ada data antrian", JLabel.CENTER);
                lblKosong.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                lblKosong.setForeground(Color.GRAY);
                // Bisa menambahkan label ini ke panel jika diperlukan
            }
            
            for (int i = 0; i < daftar.size(); i++) {
                String[] row = daftar.get(i); 
                // Format: [id_janji, id_pasien, nama_pasien, tanggal_antrian, status]
                tableModel.addRow(new Object[] {
                    i + 1,           // No urut
                    row[0],          // ID Janji
                    row[1],          // ID Pasien  
                    row[2],          // Nama Pasien
                    row[3],          // Tanggal Antrian
                    row[4]           // Status
                });
            }
            
            // Update status bar atau label info
            setTitle("Antrian Pasien - Total: " + daftar.size() + " antrian");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat data antrian: " + e.getMessage(), 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
                
            // Log error untuk debugging
            System.err.println("Error saat refresh antrian:");
            System.err.println("Message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        }
    }

    private void selesaikanAntrian() {
        // Ambil data dari baris yang dipilih
        int selectedRow = tableAntrian.getSelectedRow();
        
        if (selectedRow == -1) {
            // Jika tidak ada baris yang dipilih, minta input manual
            String idPasien = JOptionPane.showInputDialog(this, 
                "Masukkan ID Pasien yang akan diselesaikan:", 
                "Selesaikan Antrian", 
                JOptionPane.QUESTION_MESSAGE);
                
            if (idPasien == null || idPasien.trim().isEmpty()) {
                return;
            }
            
            prosesSelesaikanAntrian(idPasien.trim());
            
        } else {
            // Ambil data dari baris yang dipilih
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
            
            // Cari data pasien di antrian
            for (String[] row : daftar) {
                if (row[1].equals(idPasien)) { // row[1] adalah id_pasien
                    idJanji = row[0]; // row[0] adalah id_janji
                    namaPasien = row[2]; // row[2] adalah nama_pasien
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